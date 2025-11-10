package com.example.gestordearchivos.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.gestordearchivos.util.MimeTypeHelper
import com.example.gestordearchivos.util.PermissionManager // ¡IMPORTAR!
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.security.Permission

class FileViewModel(application: Application) : AndroidViewModel(application) {

    // Ruta del almacenamiento externo principal
    val initialPath: String = Environment.getExternalStorageDirectory().absolutePath

    private val _currentPath = MutableLiveData<String>(initialPath)
    val currentPath: LiveData<String> = _currentPath

    private val _filesList = MutableLiveData<List<File>>(emptyList())
    val filesList: LiveData<List<File>> = _filesList

    // Actualizamos el LiveData con el estado real del permiso
    private val _permissionsGranted = MutableLiveData<Boolean>(
        PermissionManager.isStoragePermissionGranted(application)
    )
    val permissionsGranted: LiveData<Boolean> = _permissionsGranted

    /**
     * Actualiza el estado del permiso. Debería llamarse desde la UI
     * cuando el permiso cambie (ej. al volver de settings).
     */
    fun updatePermissionStatus() {
        val isGranted = PermissionManager.isStoragePermissionGranted(getApplication())
        _permissionsGranted.value = isGranted

        // Si el permiso acaba de ser concedido, carga el directorio raíz
        if (isGranted && _filesList.value.isNullOrEmpty()) {
            loadDirectory(initialPath)
        }
    }

    /**
     * Carga el contenido de un directorio.
     * Esta operación debe realizarse en un hilo de IO.
     */
    fun loadDirectory(path: String) {
        // Comprobar el permiso antes de intentar leer
        if (_permissionsGranted.value != true) {
            // Si el ViewModel se entera de que no hay permiso,
            // no intenta leer y limpia la lista.
            _filesList.value = emptyList()
            // Podríamos postear un error aquí
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val directory = File(path)
                val files = directory.listFiles()

                if (files != null) {
                    // Ordenar: carpetas primero, luego archivos, ambos alfabéticamente
                    val sortedFiles = files.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))

                    withContext(Dispatchers.Main) {
                        _filesList.value = sortedFiles
                        _currentPath.value = path
                    }
                } else {
                    // Directorio nulo o no accesible
                    withContext(Dispatchers.Main) {
                        _filesList.value = emptyList()
                    }
                }
            } catch (e: SecurityException) {
                // Manejar excepción de seguridad (rutas inaccesibles)
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _filesList.value = emptyList()
                    // Si perdemos el permiso mientras la app está abierta,
                    // actualizamos el estado.
                    _permissionsGranted.value = false
                }
            } catch (e: Exception) {
                // Otro tipo de error
                e.printStackTrace()
            }
        }
    }

    /**
     * Navega al directorio padre.
     */
    fun navigateUp() {
        val current = File(_currentPath.value ?: initialPath)
        if (current.path != initialPath && current.parentFile != null) {
            loadDirectory(current.parentFile!!.path)
        }
    }

    // --- Funciones de almacenamiento persistente (Conceptuales) ---

    fun addToFavorites(file: File) {
        // TODO: Lógica para SharedPreferences o Room
        // val prefs = getApplication<Application>().getSharedPreferences("favorites", Context.MODE_PRIVATE)
        // prefs.edit().putBoolean(file.path, true).apply()
    }

    fun addRecentFile(file: File) {
        // TODO: Lógica para Room
        // val recentFile = RecentFileEntity(path = file.path, timestamp = System.currentTimeMillis())
        // recentFileRepository.insert(recentFile)
    }

    // --- Funciones de operaciones de archivo (Conceptuales) ---

    fun deleteFile(file: File) {
        if (_permissionsGranted.value != true) {
            Log.w("DeleteFile", "Permiso denegado para borrar.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                var success = false
                if (file.isDirectory) {
                    success = file.deleteRecursively()
                } else {
                    success = file.delete()
                }

                if (success) {
                    Log.i("DeleteFile", "Archivo borrado: ${file.path}")
                    // Recargar el directorio actual para reflejar el cambio
                    loadDirectory(_currentPath.value ?: initialPath)
                } else {
                    Log.e("DeleteFile", "No se pudo borrar el archivo: ${file.path}")
                    // TODO: Mostrar error al usuario (p.ej. con un Snackbar)
                }
            } catch (e: Exception) {
                Log.e("DeleteFile", "Error borrando archivo: ${file.path}", e)
            }
        }
    }

    fun renameFile(file: File, newName: String) {
        if (_permissionsGranted.value != true) {
            Log.w("RenameFile", "Permiso denegado para renombrar.")
            return
        }

        // Validación simple
        if (newName.isBlank()) {
            Log.w("RenameFile", "El nuevo nombre está vacío.")
            // TODO: Mostrar un Toast/Error al usuario
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val newFile = File(file.parent, newName)

            // Comprobar si el nuevo nombre ya existe
            if (newFile.exists()) {
                Log.e("RenameFile", "Un archivo con ese nombre ya existe.")
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "El nombre ya existe", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            try {
                val success = file.renameTo(newFile)
                if (success) {
                    Log.i("RenameFile", "Archivo renombrado a: ${newFile.path}")
                    // Recargar el directorio para reflejar el cambio
                    loadDirectory(_currentPath.value ?: initialPath)
                } else {
                    Log.e("RenameFile", "No se pudo renombrar el archivo: ${file.path}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(getApplication(), "No se pudo renombrar", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("RenameFile", "Error renombrando archivo: ${file.path}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Prepara un Intent para COMPARTIR un archivo.
     * Es similar a openFile, pero usa ACTION_SEND.
     */
    fun shareFile(context: Context, file: File) {
        if (!file.canRead()) {
            Toast.makeText(context, "No se puede compartir el archivo", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val authority = "${context.packageName}.provider"
            val fileUri = FileProvider.getUriForFile(context, authority, file)
            val mimeType = MimeTypeHelper.getMimeType(file)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Usamos un chooser para que se muestre el diálogo de "Compartir con..."
            val chooserIntent = Intent.createChooser(intent, "Compartir archivo con...")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Necesario si se llama desde fuera de una Activity

            context.startActivity(chooserIntent)

        } catch (e: Exception) {
            Log.e("ShareFile", "Error al intentar compartir el archivo: ${file.path}", e)
            Toast.makeText(context, "No se puede compartir el archivo: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
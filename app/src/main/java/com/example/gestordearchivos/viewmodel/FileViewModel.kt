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
import com.example.gestordearchivos.util.PermissionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

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

    // --- PORTAPAPELES ---
    private val _clipboard = MutableLiveData<Pair<File, ClipboardAction>?>(null)
    val clipboard: LiveData<Pair<File, ClipboardAction>?> = _clipboard
    // --- FIN PORTAPAPELES ---

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
            _filesList.value = emptyList()
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

    // --- Funciones de Portapapeles ---

    /**
     * Añade un archivo al portapapeles para COPIAR.
     */
    fun copyFileToClipboard(file: File) {
        _clipboard.value = Pair(file, ClipboardAction.COPY)
        Toast.makeText(getApplication(), "Listo para pegar", Toast.LENGTH_SHORT).show()
    }

    /**
     * Añade un archivo al portapapeles para MOVER (Cortar).
     */
    fun moveFileToClipboard(file: File) {
        _clipboard.value = Pair(file, ClipboardAction.MOVE)
        Toast.makeText(getApplication(), "Listo para pegar (Mover)", Toast.LENGTH_SHORT).show()
    }

    /**
     * Limpia el portapapeles.
     */
    fun clearClipboard() {
        _clipboard.value = null
    }

    /**
     * Pega el archivo del portapapeles en el directorio actual.
     */
    fun pasteFile() {
        val clipboardData = _clipboard.value ?: return
        val (fileToPaste, action) = clipboardData
        val destinationPath = _currentPath.value ?: initialPath
        val destinationDir = File(destinationPath)

        if (!destinationDir.canWrite()) {
            Toast.makeText(getApplication(), "Error: No se puede escribir en este directorio", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newFile = File(destinationDir, fileToPaste.name)

                if (newFile.exists()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(getApplication(), "El archivo ya existe en este directorio", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val success: Boolean = when (action) {
                    ClipboardAction.COPY -> {
                        // Copia recursiva (para carpetas)
                        fileToPaste.copyRecursively(newFile)
                    }
                    ClipboardAction.MOVE -> {
                        // Mueve el archivo
                        fileToPaste.renameTo(newFile)
                    }
                }

                if (success) {
                    // Refrescar el directorio actual
                    loadDirectory(destinationPath)
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(getApplication(), "Error al pegar el archivo", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                // Limpiar el portapapeles solo si fue una acción de MOVER
                if (action == ClipboardAction.MOVE) {
                    withContext(Dispatchers.Main) {
                        clearClipboard()
                    }
                }
            }
        }
    }


    // --- Funciones de operaciones de archivo ---

    fun addToFavorites(file: File) {
        // TODO: Lógica para SharedPreferences o Room
    }

    fun addRecentFile(file: File) {
        // TODO: Lógica para Room
    }

    /**
     * Borra un archivo o directorio recursivamente.
     */
    fun deleteFile(file: File) {
        if (_permissionsGranted.value != true) {
            Log.w("DeleteFile", "Permiso denegado para borrar.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = if (file.isDirectory) {
                    file.deleteRecursively()
                } else {
                    file.delete()
                }

                if (success) {
                    Log.i("DeleteFile", "Archivo borrado: ${file.path}")
                    // Recargar el directorio actual para reflejar el cambio
                    loadDirectory(_currentPath.value ?: initialPath)
                } else {
                    Log.e("DeleteFile", "No se pudo borrar el archivo: ${file.path}")
                }
            } catch (e: Exception) {
                Log.e("DeleteFile", "Error borrando archivo: ${file.path}", e)
            }
        }
    }

    /**
     * Renombra un archivo o directorio.
     */
    fun renameFile(file: File, newName: String) {
        if (_permissionsGranted.value != true) {
            Log.w("RenameFile", "Permiso denegado para renombrar.")
            return
        }

        if (newName.isBlank()) {
            Log.w("RenameFile", "El nuevo nombre está vacío.")
            Toast.makeText(getApplication(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val newFile = File(file.parent, newName)

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
                    // Recargar el directorio
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

            val chooserIntent = Intent.createChooser(intent, "Compartir archivo con...")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(chooserIntent)

        } catch (e: Exception) {
            Log.e("ShareFile", "Error al intentar compartir el archivo: ${file.path}", e)
            Toast.makeText(context, "No se puede compartir el archivo: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
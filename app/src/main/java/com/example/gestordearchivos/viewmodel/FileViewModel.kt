package com.example.gestordearchivos.viewmodel

import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidxs.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

    // TODO: Manejar el estado de los permisos
    private val _permissionsGranted = MutableLiveData<Boolean>(false)
    val permissionsGranted: LiveData<Boolean> = _permissionsGranted

    /**
     * Carga el contenido de un directorio.
     * Esta operación debe realizarse en un hilo de IO.
     */
    fun loadDirectory(path: String) {
        // TODO: Comprobar los permisos antes de intentar leer
        // if (!permissionsGranted.value!!) {
        //     // Pedir permisos
        //     return
        // }

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
                    // TODO: Mostrar un error al usuario (p.ej. con un Snackbar)
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // TODO: Implementar validación y diálogo de confirmación
                if (file.deleteRecursively()) {
                    // Recargar el directorio actual
                    loadDirectory(_currentPath.value ?: initialPath)
                } else {
                    // TODO: Mostrar error
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
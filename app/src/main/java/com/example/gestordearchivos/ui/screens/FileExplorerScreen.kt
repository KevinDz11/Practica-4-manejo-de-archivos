package com.example.gestordearchivos.ui.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gestordearchivos.ui.AppRoutes
import com.example.gestordearchivos.ui.components.FileBreadcrumbs
import com.example.gestordearchivos.ui.components.FileOptionsDialog
import com.example.gestordearchivos.ui.components.RenameFileDialog
import com.example.gestordearchivos.util.MimeTypeHelper
import com.example.gestordearchivos.viewmodel.FileViewModel
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

// Formateador de fecha para los metadatos
private val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

// --- FUNCIONES HELPER PARA TIPOS DE ARCHIVO ---
private fun isTextFile(file: File): Boolean {
    val extension = file.extension.lowercase(Locale.getDefault())
    // JSON y XML también son texto
    return extension in listOf("txt", "md", "log", "json", "xml", "kt", "java", "py")
}

private fun isImageFile(file: File): Boolean {
    val extension = file.extension.lowercase(Locale.getDefault())
    return extension in listOf("jpg", "jpeg", "png", "bmp", "webp", "gif")
}

/**
 * Abre un archivo usando un Intent ACTION_VIEW.
 * Este es el fallback para tipos de archivo que la app no abre nativamente.
 */
private fun openFileWithIntent(context: Context, file: File) {
    try {
        // 1. Obtener la autoridad del FileProvider (debe coincidir con el Manifest)
        val authority = "${context.packageName}.provider"

        // 2. Obtener la URI segura para el archivo
        val fileUri = FileProvider.getUriForFile(context, authority, file)

        // 3. Obtener el tipo MIME del archivo
        val mimeType = MimeTypeHelper.getMimeType(file)

        // 4. Crear el Intent para ver el archivo
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, mimeType)
            // 5. Dar permisos temporales a la app que reciba el Intent
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        // 6. Iniciar la actividad (el selector "Abrir con...")
        context.startActivity(intent)

    } catch (e: Exception) {
        // Manejar excepciones comunes:
        // - ActivityNotFoundException: No hay ninguna app que pueda abrir este tipo de archivo.
        // - IllegalArgumentException: Problema con el FileProvider (a menudo path incorrecto).
        Log.e("OpenFile", "Error al intentar abrir el archivo: ${file.path}", e)
        Toast.makeText(context, "No se puede abrir el archivo: ${e.message}", Toast.LENGTH_LONG).show()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileExplorerScreen(
    navController: NavController, // Controlador para navegar a otros visores
    fileViewModel: FileViewModel = viewModel()
) {
    // Observamos los estados del ViewModel
    val currentPath by fileViewModel.currentPath.observeAsState(fileViewModel.initialPath)
    val filesList by fileViewModel.filesList.observeAsState(emptyList())
    val clipboardData by fileViewModel.clipboard.observeAsState(null)
    val context = LocalContext.current

    // --- ESTADOS PARA LOS DIÁLOGOS ---
    // 1. Estado para saber qué archivo seleccionamos
    var selectedFile by remember { mutableStateOf<File?>(null) }

    // 2. Estado para mostrar/ocultar el BottomSheet de opciones
    var showOptionsSheet by rememberSaveable { mutableStateOf(false) }

    // 3. Estado para mostrar/ocultar el diálogo de confirmación de borrado
    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    // 4. Estado para el diálogo de renombrar
    var showRenameDialog by rememberSaveable { mutableStateOf(false) }


    // --- LÓGICA DE LOS DIÁLOGOS ---

    // 1. Si el estado `showOptionsSheet` es verdadero, mostramos el BottomSheet
    if (showOptionsSheet) {
        if (selectedFile != null) {
            FileOptionsDialog(
                file = selectedFile!!,
                onDismiss = { showOptionsSheet = false },
                onShareClick = { file ->
                    fileViewModel.shareFile(context, file)
                    // onDismiss se llama dentro del dialog
                },
                onDeleteClick = {
                    // En lugar de borrar directo, mostramos el diálogo de confirmación
                    showDeleteConfirmDialog = true
                    // onDismiss se llama dentro del dialog
                },
                onRenameClick = {
                    // Mostramos el diálogo de renombrar
                    showRenameDialog = true
                    // onDismiss se llama dentro del dialog
                },
                onCopyClick = { file ->
                    fileViewModel.copyFileToClipboard(file)
                    // onDismiss se llama dentro del dialog
                },
                onMoveClick = { file ->
                    fileViewModel.moveFileToClipboard(file)
                    // onDismiss se llama dentro del dialog
                }
            )
        } else {
            // Si el archivo es nulo (no debería pasar), ocultamos la hoja
            showOptionsSheet = false
        }
    }

    // 2. Si el estado `showDeleteConfirmDialog` es verdadero, mostramos el diálogo de alerta
    if (showDeleteConfirmDialog) {
        if (selectedFile != null) {
            DeleteConfirmationDialog(
                file = selectedFile!!,
                onDismiss = { showDeleteConfirmDialog = false },
                onConfirm = {
                    fileViewModel.deleteFile(selectedFile!!)
                    showDeleteConfirmDialog = false
                    selectedFile = null // Limpiar selección
                }
            )
        } else {
            // Si el archivo es nulo, ocultamos
            showDeleteConfirmDialog = false
        }
    }

    // 3. Diálogo de Renombrar
    if (showRenameDialog) {
        if (selectedFile != null) {
            RenameFileDialog(
                file = selectedFile!!,
                onDismiss = {
                    showRenameDialog = false
                    selectedFile = null // Limpiar selección
                },
                onConfirm = { newName ->
                    fileViewModel.renameFile(selectedFile!!, newName)
                    showRenameDialog = false
                    selectedFile = null // Limpiar selección
                }
            )
        } else {
            showRenameDialog = false
        }
    }

    // --- UI PRINCIPAL (Scaffold) ---
    // Efecto para cargar el directorio inicial (o cuando el permiso esté listo)
    LaunchedEffect(Unit) {
        fileViewModel.loadDirectory(currentPath)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Reemplazamos el Text simple con los Breadcrumbs interactivos
                    FileBreadcrumbs(
                        currentPath = currentPath,
                        rootPath = fileViewModel.initialPath,
                        onPathClick = { path ->
                            // Al hacer clic en un segmento, cargamos ese directorio
                            fileViewModel.loadDirectory(path)
                        }
                    )
                },
                // Botón para ir atrás
                navigationIcon = {
                    if (currentPath != fileViewModel.initialPath) {
                        IconButton(onClick = { fileViewModel.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Navegar hacia arriba"
                            )
                        }
                    }
                },
                // Acciones de la barra superior (Pegar / Cancelar)
                actions = {
                    // Si hay algo en el portapapeles, mostrar botones
                    if (clipboardData != null) {
                        // Botón de Pegar
                        IconButton(onClick = { fileViewModel.pasteFile() }) {
                            Icon(Icons.Default.ContentPaste, "Pegar")
                        }
                        // Botón de Cancelar Pegado
                        IconButton(onClick = { fileViewModel.clearClipboard() }) {
                            Icon(Icons.Default.Cancel, "Cancelar")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (filesList.isEmpty()) {
                // Muestra un indicador de carga o de carpeta vacía
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Carpeta vacía o cargando...")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filesList) { file ->
                        FileListItem(
                            file = file,
                            onFileClick = {
                                // --- LÓGICA DE CLIC ACTUALIZADA ---
                                if (file.isDirectory) {
                                    fileViewModel.loadDirectory(file.path)
                                } else {
                                    // URL-encode del path para que sea seguro pasarlo como argumento
                                    val encodedPath = URLEncoder.encode(file.path, StandardCharsets.UTF_8.toString())

                                    when {
                                        isTextFile(file) -> {
                                            navController.navigate("${AppRoutes.TEXT_VIEWER}?path=$encodedPath")
                                        }
                                        isImageFile(file) -> {
                                            navController.navigate("${AppRoutes.IMAGE_VIEWER}?path=$encodedPath")
                                        }
                                        else -> {
                                            // Fallback para otros tipos de archivo
                                            openFileWithIntent(context, file)
                                        }
                                    }
                                }
                            },
                            onShowOptions = {
                                // ¡ACCIÓN! Al hacer clic en los 3 puntos:
                                // 1. Guardamos el archivo seleccionado
                                selectedFile = file
                                // 2. Activamos el BottomSheet
                                showOptionsSheet = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FileListItem(
    file: File,
    onFileClick: () -> Unit,
    onShowOptions: () -> Unit
) {
    val icon = if (file.isDirectory) Icons.Default.Folder else Icons.Default.Description
    // Evita crash si el archivo es inaccesible (length=0) y previene división por cero en formatFileSize
    val metadata = if (file.canRead()) {
        "${formatFileSize(file.length())} | ${dateFormatter.format(Date(file.lastModified()))}"
    } else {
        "No accesible | ${dateFormatter.format(Date(file.lastModified()))}"
    }


    ListItem(
        headlineContent = { Text(file.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingContent = { Text(metadata, style = MaterialTheme.typography.bodySmall) },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = if (file.isDirectory) "Directorio" else "Archivo"
            )
        },
        trailingContent = {
            IconButton(onClick = onShowOptions) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Opciones")
            }
        },
        modifier = Modifier.clickable(onClick = onFileClick)
    )
}

// Función de utilidad para formatear el tamaño del archivo
private fun formatFileSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    // Previene un error de Math.log10(0)
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
    // Asegura que digitGroups no esté fuera de los límites del array
    val aDigitGroups = if (digitGroups > units.size - 1) units.size - 1 else digitGroups
    return String.format(Locale.getDefault(), "%.1f %s", size / Math.pow(1024.0, aDigitGroups.toDouble()), units[aDigitGroups])
}

// --- DIÁLOGO DE CONFIRMACIÓN DE BORRADO ---
@Composable
private fun DeleteConfirmationDialog(
    file: File,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar borrado") },
        text = {
            Text(
                "¿Estás seguro de que quieres borrar \"${file.name}\"?\n\n" +
                        if (file.isDirectory) "Esta acción es permanente y borrará todo su contenido."
                        else "Esta acción es permanente."
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Borrar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
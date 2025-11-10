package com.example.gestordearchivos.ui.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
// ¡IMPORTS NUEVOS/MODIFICADOS!
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
// Import de Coil
import coil.compose.AsyncImage
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
        // ... (código de openFileWithIntent sin cambios)
        val authority = "${context.packageName}.provider"
        val fileUri = FileProvider.getUriForFile(context, authority, file)
        val mimeType = MimeTypeHelper.getMimeType(file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, mimeType)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)
    } catch (e: Exception) {
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
    // ... (Estados, Lógica de Diálogos, y Scaffold/TopAppBar NO CAMBIAN) ...
    // ... (El código es idéntico al que te pasé en el paso anterior hasta el FileListItem) ...
    val currentPath by fileViewModel.currentPath.observeAsState(fileViewModel.initialPath)
    val filesList by fileViewModel.filesList.observeAsState(emptyList())
    val clipboardData by fileViewModel.clipboard.observeAsState(null)
    val context = LocalContext.current
    var selectedFile by remember { mutableStateOf<File?>(null) }
    var showOptionsSheet by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }
    var showRenameDialog by rememberSaveable { mutableStateOf(false) }


    // --- LÓGICA DE LOS DIÁLOGOS ---
    if (showOptionsSheet) {
        if (selectedFile != null) {
            FileOptionsDialog(
                file = selectedFile!!,
                onDismiss = { showOptionsSheet = false },
                onShareClick = { file -> fileViewModel.shareFile(context, file) },
                onDeleteClick = { showDeleteConfirmDialog = true },
                onRenameClick = { showRenameDialog = true },
                onCopyClick = { file -> fileViewModel.copyFileToClipboard(file) },
                onMoveClick = { file -> fileViewModel.moveFileToClipboard(file) }
            )
        } else {
            showOptionsSheet = false
        }
    }
    if (showDeleteConfirmDialog) {
        if (selectedFile != null) {
            DeleteConfirmationDialog(
                file = selectedFile!!,
                onDismiss = { showDeleteConfirmDialog = false },
                onConfirm = {
                    fileViewModel.deleteFile(selectedFile!!)
                    showDeleteConfirmDialog = false
                    selectedFile = null
                }
            )
        } else {
            showDeleteConfirmDialog = false
        }
    }
    if (showRenameDialog) {
        if (selectedFile != null) {
            RenameFileDialog(
                file = selectedFile!!,
                onDismiss = {
                    showRenameDialog = false
                    selectedFile = null
                },
                onConfirm = { newName ->
                    fileViewModel.renameFile(selectedFile!!, newName)
                    showRenameDialog = false
                    selectedFile = null
                }
            )
        } else {
            showRenameDialog = false
        }
    }

    // --- UI PRINCIPAL (Scaffold) ---
    LaunchedEffect(Unit) {
        fileViewModel.loadDirectory(currentPath)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    FileBreadcrumbs(
                        currentPath = currentPath,
                        rootPath = fileViewModel.initialPath,
                        onPathClick = { path ->
                            fileViewModel.loadDirectory(path)
                        }
                    )
                },
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
                actions = {
                    if (clipboardData != null) {
                        IconButton(onClick = { fileViewModel.pasteFile() }) {
                            Icon(Icons.Default.ContentPaste, "Pegar")
                        }
                        IconButton(onClick = { fileViewModel.clearClipboard() }) {
                            Icon(Icons.Default.Cancel, "Cancelar")
                        }
                    } else {
                        IconButton(onClick = { navController.navigate(AppRoutes.SETTINGS) }) {
                            Icon(Icons.Default.Settings, "Ajustes")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (filesList.isEmpty()) {
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
                                if (file.isDirectory) {
                                    fileViewModel.loadDirectory(file.path)
                                } else {
                                    val encodedPath = URLEncoder.encode(file.path, StandardCharsets.UTF_8.toString())
                                    when {
                                        isTextFile(file) -> {
                                            navController.navigate("${AppRoutes.TEXT_VIEWER}?path=$encodedPath")
                                        }
                                        isImageFile(file) -> {
                                            navController.navigate("${AppRoutes.IMAGE_VIEWER}?path=$encodedPath")
                                        }
                                        else -> {
                                            openFileWithIntent(context, file)
                                        }
                                    }
                                }
                            },
                            onShowOptions = {
                                selectedFile = file
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
    // --- LÓGICA DE METADATOS (SIN CAMBIOS) ---
    val metadata = if (file.canRead()) {
        "${formatFileSize(file.length())} | ${dateFormatter.format(Date(file.lastModified()))}"
    } else {
        "No accesible | ${dateFormatter.format(Date(file.lastModified()))}"
    }

    ListItem(
        headlineContent = { Text(file.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingContent = { Text(metadata, style = MaterialTheme.typography.bodySmall) },

        // --- ¡CAMBIO GRÁFICO AQUÍ! ---
        // Modificamos el leadingContent para mostrar miniaturas
        leadingContent = {
            when {
                // Caso 1: Es un Directorio
                file.isDirectory -> {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = "Directorio",
                        modifier = Modifier.size(40.dp) // Tamaño fijo
                    )
                }

                // Caso 2: Es una Imagen (¡NUEVO!)
                isImageFile(file) -> {
                    // AsyncImage (de Coil) cargará la miniatura y la cacheará
                    AsyncImage(
                        model = file, // Carga el 'File' directamente
                        contentDescription = file.name,
                        contentScale = ContentScale.Crop, // Recorta para llenar el espacio
                        modifier = Modifier
                            .size(40.dp) // Mismo tamaño que los iconos
                            .clip(RoundedCornerShape(4.dp)) // Bordes redondeados
                    )
                }

                // Caso 3: Otro tipo de archivo
                else -> {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "Archivo",
                        modifier = Modifier.size(40.dp) // Tamaño fijo
                    )
                }
            }
        },
        // --- FIN DEL CAMBIO ---

        trailingContent = {
            IconButton(onClick = onShowOptions) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Opciones")
            }
        },
        modifier = Modifier.clickable(onClick = onFileClick)
    )
}

// ... (formatFileSize y DeleteConfirmationDialog NO CAMBIAN) ...

// Función de utilidad para formatear el tamaño del archivo
private fun formatFileSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
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
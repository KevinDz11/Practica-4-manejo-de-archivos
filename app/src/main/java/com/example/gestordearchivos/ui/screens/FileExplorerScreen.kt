package com.example.gestordearchivos.ui.screens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fileexplorer.viewmodel.FileViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// Formateador de fecha para los metadatos
private val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileExplorerScreen(
    fileViewModel: FileViewModel = viewModel()
) {
    // Observamos los estados del ViewModel
    val currentPath by fileViewModel.currentPath.observeAsState(fileViewModel.initialPath)
    val filesList by fileViewModel.filesList.observeAsState(emptyList())

    // Efecto para cargar el directorio inicial (o cuando el permiso esté listo)
    LaunchedEffect(Unit) {
        fileViewModel.loadDirectory(currentPath)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Aquí irían los "Breadcrumbs"
                    Text(text = currentPath, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                // Botón para ir atrás (implementar lógica en ViewModel)
                navigationIcon = {
                    // IconButton(onClick = { fileViewModel.navigateUp() }) { ... }
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
                                if (file.isDirectory) {
                                    fileViewModel.loadDirectory(file.path)
                                } else {
                                    // TODO: Lógica para abrir el archivo
                                    // (Visor de texto, imagen, o diálogo "Abrir con")
                                }
                            },
                            onShowOptions = {
                                // TODO: Mostrar menú (renombrar, borrar, mover, etc.)
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
    val metadata = "${formatFileSize(file.length())} | ${dateFormatter.format(Date(file.lastModified()))}"

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
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format(Locale.getDefault(), "%.1f %s", size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}
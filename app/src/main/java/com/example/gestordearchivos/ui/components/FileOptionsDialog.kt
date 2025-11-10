package com.example.gestordearchivos.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
// AGREGADO: Nuevos iconos
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileOptionsDialog(
    file: File,
    onDismiss: () -> Unit,
    onShareClick: (File) -> Unit,
    onDeleteClick: (File) -> Unit,
    onRenameClick: (File) -> Unit,
    // AGREGADO: Nuevas acciones
    onCopyClick: (File) -> Unit,
    onMoveClick: (File) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column {
            ListItem(
                headlineContent = { Text(file.name) },
                supportingContent = { Text("Opciones del archivo") }
            )

            // Opción: Compartir
            ListItem(
                headlineContent = { Text("Compartir") },
                leadingContent = { Icon(Icons.Default.Share, "Compartir") },
                modifier = Modifier.clickable {
                    onShareClick(file)
                    onDismiss()
                }
            )

            // --- AÑADIDO: Copiar ---
            ListItem(
                headlineContent = { Text("Copiar") },
                leadingContent = { Icon(Icons.Default.ContentCopy, "Copiar") },
                modifier = Modifier.clickable {
                    onCopyClick(file)
                    onDismiss()
                }
            )

            // --- AÑADIDO: Mover ---
            ListItem(
                headlineContent = { Text("Mover (Cortar)") },
                leadingContent = { Icon(Icons.Default.DriveFileMove, "Mover") },
                modifier = Modifier.clickable {
                    onMoveClick(file)
                    onDismiss()
                }
            )

            // Opción: Renombrar
            ListItem(
                headlineContent = { Text("Renombrar") },
                leadingContent = { Icon(Icons.Default.Edit, "Renombrar") },
                modifier = Modifier.clickable {
                    onRenameClick(file)
                    onDismiss()
                }
            )

            // Opción: Borrar
            ListItem(
                headlineContent = { Text("Borrar") },
                leadingContent = { Icon(Icons.Default.Delete, "Borrar") },
                modifier = Modifier.clickable {
                    onDeleteClick(file)
                    onDismiss()
                }
            )
        }
    }
}
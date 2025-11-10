package com.example.gestordearchivos.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
    onCopyClick: (File) -> Unit,
    onMoveClick: (File) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    // Obtenemos el color primario del tema (Guinda o Azul)
    val iconColor = MaterialTheme.colorScheme.primary

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column {
            // Título (nombre del archivo)
            ListItem(
                headlineContent = { Text(file.name) },
                supportingContent = { Text("Opciones del archivo") }
            )

            // Opción: Compartir
            ListItem(
                headlineContent = { Text("Compartir") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartir",
                        tint = iconColor // Aplicamos el tinte
                    )
                },
                modifier = Modifier.clickable {
                    onShareClick(file)
                    onDismiss()
                }
            )

            // Opción: Copiar
            ListItem(
                headlineContent = { Text("Copiar") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copiar",
                        tint = iconColor // Aplicamos el tinte
                    )
                },
                modifier = Modifier.clickable {
                    onCopyClick(file)
                    onDismiss()
                }
            )

            // Opción: Mover
            ListItem(
                headlineContent = { Text("Mover (Cortar)") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.DriveFileMove,
                        contentDescription = "Mover",
                        tint = iconColor // Aplicamos el tinte
                    )
                },
                modifier = Modifier.clickable {
                    onMoveClick(file)
                    onDismiss()
                }
            )

            // Opción: Renombrar
            ListItem(
                headlineContent = { Text("Renombrar") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Renombrar",
                        tint = iconColor // Aplicamos el tinte
                    )
                },
                modifier = Modifier.clickable {
                    onRenameClick(file)
                    onDismiss()
                }
            )

            // Opción: Borrar
            ListItem(
                headlineContent = { Text("Borrar") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Borrar",
                        tint = iconColor // Aplicamos el tinte
                    )
                },
                modifier = Modifier.clickable {
                    onDeleteClick(file)
                    onDismiss()
                }
            )
        }
    }
}
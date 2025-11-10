package com.example.gestordearchivos.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
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
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileOptionsDialog(
    file: File,
    onDismiss: () -> Unit,
    onShareClick: (File) -> Unit,
    onDeleteClick: (File) -> Unit,
    onRenameClick: (File) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

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
                        contentDescription = "Compartir"
                    )
                },
                modifier = androidx.compose.ui.Modifier.clickable {
                    onShareClick(file)
                    onDismiss()
                }
            )

            // Opción: Renombrar (la dejaremos como TODO por ahora)
            ListItem(
                headlineContent = { Text("Renombrar") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Renombrar"
                    )
                },
                modifier = androidx.compose.ui.Modifier.clickable {
                    // TODO: Implementar lógica de renombrar
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
                        contentDescription = "Borrar"
                    )
                },
                modifier = androidx.compose.ui.Modifier.clickable {
                    onDeleteClick(file)
                    onDismiss()
                }
            )
        }
    }
}
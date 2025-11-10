package com.example.gestordearchivos.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.io.File

// Data class interna para manejar cada segmento de la ruta
private data class BreadcrumbItem(val name: String, val path: String)

@Composable
fun FileBreadcrumbs(
    currentPath: String,
    rootPath: String,
    onPathClick: (String) -> Unit
) {
    // Usamos 'remember' para recalcular los segmentos solo si la ruta cambia
    val breadcrumbs = remember(currentPath, rootPath) {
        parsePathToBreadcrumbs(currentPath, rootPath)
    }

    // LazyRow permite el scroll horizontal si la ruta es muy larga
    LazyRow(
        modifier = Modifier.padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(breadcrumbs) { index, item ->
            val isLast = index == breadcrumbs.lastIndex

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.name,
                    color = if (isLast) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary,
                    fontWeight = if (isLast) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .clickable(enabled = !isLast) { // Solo se puede hacer clic si NO es el último
                            onPathClick(item.path)
                        }
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                )

                if (!isLast) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Separador",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Función helper para convertir un String de ruta en una lista de segmentos (BreadcrumbItem)
 */
private fun parsePathToBreadcrumbs(currentPath: String, rootPath: String): List<BreadcrumbItem> {
    val items = mutableListOf<BreadcrumbItem>()

    // 1. Añadir el item raíz "Inicio"
    items.add(BreadcrumbItem("Inicio", rootPath))

    // Si estamos en la raíz, no hay nada más que hacer
    if (currentPath == rootPath) {
        return items
    }

    // 2. Obtener la ruta relativa (ej. "/Download/Documentos")
    val relativePath = currentPath.removePrefix(rootPath)

    // 3. Separar en segmentos (ej. ["Download", "Documentos"])
    val segments = relativePath.split(File.separatorChar).filter { it.isNotEmpty() }

    var currentSegmentPath = rootPath
    for (segment in segments) {
        // Acumular la ruta para cada segmento
        currentSegmentPath += File.separatorChar + segment
        items.add(BreadcrumbItem(segment, currentSegmentPath))
    }

    return items
}
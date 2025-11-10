package com.example.gestordearchivos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
// Importar la nueva librería
import me.saket.telephoto.zoomable.image.coil.ZoomableCoilImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageViewerScreen(
    filePath: String,
    onNavigateBack: () -> Unit
) {
    val file = remember { File(filePath) }

    // Estado para la rotación
    var rotation by remember { mutableFloatStateOf(0f) }
    // Estado para el zoom (controlado por la librería)
    val zoomState = rememberZoomableState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(file.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        },
        // --- ¡AÑADIDO! Barra inferior para controles ---
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Botón de rotar a la izquierda
                    IconButton(onClick = { rotation -= 90f }) {
                        Icon(Icons.Default.RotateLeft, "Rotar izquierda")
                    }
                    // Botón de rotar a la derecha
                    IconButton(onClick = { rotation += 90f }) {
                        Icon(Icons.Default.RotateRight, "Rotar derecha")
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // --- ¡CAMBIO GRANDE AQUÍ! ---
            // Usamos ZoomableCoilImage en lugar de SubcomposeAsyncImage
            ZoomableCoilImage(
                model = file, // Carga el 'File' directamente
                contentDescription = file.name,
                modifier = Modifier
                    .fillMaxSize()
                    // Aplicamos la rotación
                    .graphicsLayer {
                        rotationZ = rotation
                    },
                state = zoomState,
                loading = {
                    CircularProgressIndicator()
                }
            )
            // --- FIN DEL CAMBIO ---
        }
    }
}
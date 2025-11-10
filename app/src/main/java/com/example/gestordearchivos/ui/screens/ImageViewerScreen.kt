package com.example.gestordearchivos.ui.screens

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage // Importamos el AsyncImage normal de Coil
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageViewerScreen(
    filePath: String,
    onNavigateBack: () -> Unit
) {
    val file = remember { File(filePath) }

    // --- INICIO DE LA NUEVA LÓGICA DE ZOOM/PAN ---

    // 1. Estados para guardar la transformación
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    // El estado de rotación que ya tenías
    var rotation by remember { mutableFloatStateOf(0f) }

    // 2. Estado que maneja los gestos (pellizcar, arrastrar)
    val transformState = rememberTransformableState { zoomChange, panChange, rotationChange ->
        // Actualizamos la escala, limitando el zoom entre 1x y 5x
        scale = (scale * zoomChange).coerceIn(1f, 5f)

        // Si la escala es mayor a 1, permitimos arrastrar (pan)
        if (scale > 1f) {
            offset += panChange
        }

        // Opcional: si quieres que el gesto de dos dedos también rote
        // rotation += rotationChange
    }

    // 3. Lógica para centrar la imagen si la escala vuelve a ser 1
    if (scale == 1f) {
        offset = Offset.Zero
    }
    // --- FIN DE LA NUEVA LÓGICA ---

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
                    // Botón para resetear el zoom y la posición
                    IconButton(onClick = {
                        scale = 1f
                        offset = Offset.Zero
                    }) {
                        Icon(Icons.Default.ZoomOutMap, "Resetear Zoom")
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                // 4. Aplicamos el detector de gestos al Box contenedor
                .transformable(state = transformState),
            contentAlignment = Alignment.Center
        ) {
            // 5. Usamos el AsyncImage normal de Coil
            AsyncImage(
                model = file,
                contentDescription = file.name,
                contentScale = ContentScale.Fit, // Usamos Fit para que la imagen se vea completa
                modifier = Modifier
                    .fillMaxSize()
                    // 6. Aplicamos las transformaciones con graphicsLayer
                    // Esto es lo que mueve, escala y rota la imagen
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                        rotationZ = rotation
                    }
            )
        }
    }
}
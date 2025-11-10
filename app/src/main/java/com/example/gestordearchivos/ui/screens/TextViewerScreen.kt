package com.example.gestordearchivos.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextViewerScreen(
    filePath: String,
    onNavigateBack: () -> Unit
) {
    val file = remember { File(filePath) }
    var fileContent by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    // Efecto para leer el archivo del disco en un hilo de fondo
    LaunchedEffect(file) {
        try {
            // Leer el archivo en el hilo de IO
            val content = withContext(Dispatchers.IO) {
                if (file.canRead()) {
                    file.readText()
                } else {
                    throw Exception("No se puede leer el archivo.")
                }
            }
            fileContent = content
        } catch (e: Exception) {
            Log.e("TextViewer", "Error al leer el archivo: ${file.path}", e)
            error = "Error al abrir el archivo: ${e.message}"
        }
    }

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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                // Estado de carga
                fileContent == null && error == null -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                // Estado de error
                error != null -> {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                }
                // Estado de éxito
                fileContent != null -> {
                    // Usamos un TextField de solo lectura para un buen rendimiento
                    // y scroll nativo.
                    TextField(
                        value = fileContent!!,
                        onValueChange = {}, // No se puede cambiar
                        readOnly = true,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
package com.example.gestordearchivos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gestordearchivos.ui.screens.FileExplorerScreen
import com.example.gestordearchivos.ui.theme.FileExplorerTheme // Importa tu tema personalizado
import com.example.gestordearchivos.ui.theme.AppThemeType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Aquí seleccionas el tema. ¡Puedes hacerlo dinámico más tarde!
            FileExplorerTheme(
                theme = AppThemeType.GUINDA // O AppThemeType.AZUL
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // ¡Aquí llamas a tu pantalla principal!
                    FileExplorerScreen()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GestorDeArchivosTheme {
        Greeting("Android")
    }
}
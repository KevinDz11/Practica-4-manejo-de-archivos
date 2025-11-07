package com.example.gestordearchivos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.gestordearchivos.ui.screens.FileExplorerScreen
import com.example.gestordearchivos.ui.screens.RequestPermissionScreen // ¡IMPORTAR!
import com.example.gestordearchivos.ui.theme.FileExplorerTheme
import com.example.gestordearchivos.ui.theme.AppThemeType
import com.example.gestordearchivos.util.PermissionManager // ¡IMPORTAR!

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Estado para rastrear si el permiso está concedido
            // Usamos rememberSaveable para que sobreviva a rotaciones
            var hasPermission by rememberSaveable { mutableStateOf(false) }
            val context = LocalContext.current

            // Lanzador para el resultado de la pantalla de configuración de permisos
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = {
                    // Cuando el usuario regresa de la configuración,
                    // volvemos a comprobar el permiso.
                    hasPermission = PermissionManager.isStoragePermissionGranted(context)
                }
            )

            // Comprobar el permiso al iniciar la app
            // LaunchedEffect se ejecuta una sola vez (gracias a 'Unit')
            LaunchedEffect(Unit) {
                hasPermission = PermissionManager.isStoragePermissionGranted(context)
            }

            FileExplorerTheme(
                theme = AppThemeType.GUINDA // O AppThemeType.AZUL
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (hasPermission) {
                        // Si tenemos permiso, mostramos el explorador
                        FileExplorerScreen()
                    } else {
                        // Si no, mostramos la pantalla de solicitud
                        RequestPermissionScreen(
                            onGrantPermissionClick = {
                                // Lanzamos el intent para ir a configuración
                                val intent = PermissionManager.getStoragePermissionIntent(context)
                                permissionLauncher.launch(intent)
                            }
                        )
                    }
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
    FileExplorerTheme {
        Greeting("Android")
    }
}
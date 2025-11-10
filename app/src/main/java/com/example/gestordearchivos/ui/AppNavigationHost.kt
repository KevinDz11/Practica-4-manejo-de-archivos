package com.example.gestordearchivos.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gestordearchivos.ui.screens.FileExplorerScreen
import com.example.gestordearchivos.ui.screens.ImageViewerScreen
import com.example.gestordearchivos.ui.screens.SettingsScreen
import com.example.gestordearchivos.ui.screens.TextViewerScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

// Definimos las "rutas" como constantes para evitar errores
object AppRoutes {
    const val FILE_EXPLORER = "file_explorer"
    const val TEXT_VIEWER = "text_viewer"
    const val IMAGE_VIEWER = "image_viewer"
    const val SETTINGS = "settings" // Nueva ruta
}

@Composable
fun AppNavigationHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.FILE_EXPLORER // Empezamos en el explorador
    ) {
        // --- Ruta: Explorador de Archivos ---
        composable(AppRoutes.FILE_EXPLORER) {
            FileExplorerScreen(navController = navController)
        }

        // --- Ruta: Visor de Texto ---
        // Acepta una ruta de archivo como argumento: "text_viewer?path={filePath}"
        composable(
            route = "${AppRoutes.TEXT_VIEWER}?path={path}",
            arguments = listOf(navArgument("path") { type = NavType.StringType })
        ) { navBackStackEntry ->
            val encodedPath = navBackStackEntry.arguments?.getString("path") ?: ""
            // Decodificamos la ruta del archivo (importante por los caracteres especiales)
            val filePath = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8.toString())

            TextViewerScreen(
                filePath = filePath,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- Ruta: Visor de Imágenes ---
        // Acepta una ruta de archivo como argumento: "image_viewer?path={filePath}"
        composable(
            route = "${AppRoutes.IMAGE_VIEWER}?path={path}",
            arguments = listOf(navArgument("path") { type = NavType.StringType })
        ) { navBackStackEntry ->
            val encodedPath = navBackStackEntry.arguments?.getString("path") ?: ""
            val filePath = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8.toString())

            ImageViewerScreen(
                filePath = filePath,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- Ruta: Ajustes ---
        composable(AppRoutes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
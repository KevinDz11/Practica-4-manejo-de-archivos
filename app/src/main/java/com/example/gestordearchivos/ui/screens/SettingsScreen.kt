package com.example.gestordearchivos.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestordearchivos.ui.theme.AppThemeType
import com.example.gestordearchivos.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    themeViewModel: ThemeViewModel = viewModel()
) {
    // Observamos el tema actual desde el mismo ViewModel
    val currentTheme by themeViewModel.theme.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") },
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
            // Título de la sección
            ListItem(
                headlineContent = { Text("Apariencia", style = MaterialTheme.typography.titleMedium) },
                leadingContent = { Icon(Icons.Default.InvertColors, "Apariencia") }
            )

            // Opción Guinda
            SelectableThemeRow(
                text = "Tema Guinda (IPN)",
                isSelected = currentTheme == AppThemeType.GUINDA,
                onClick = { themeViewModel.setTheme(AppThemeType.GUINDA) }
            )

            // Opción Azul
            SelectableThemeRow(
                text = "Tema Azul (ESCOM)",
                isSelected = currentTheme == AppThemeType.AZUL,
                onClick = { themeViewModel.setTheme(AppThemeType.AZUL) }
            )
        }
    }
}

/**
 * Un Composable reutilizable para mostrar una fila con un RadioButton
 */
@Composable
private fun SelectableThemeRow(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick
        )
        Spacer(Modifier.width(16.dp))
        Text(text)
    }
}
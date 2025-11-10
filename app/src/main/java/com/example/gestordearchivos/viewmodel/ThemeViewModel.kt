package com.example.gestordearchivos.viewmodel

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestordearchivos.ui.theme.AppThemeType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Extensión para crear una instancia de DataStore (una sola vez)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    // La clave para guardar nuestro tema en DataStore
    private val THEME_KEY = stringPreferencesKey("app_theme")

    // Expone un StateFlow que emite el tema actual (GUINDA o AZUL)
    val theme: StateFlow<AppThemeType> = getApplication<Application>().dataStore.data
        .map { preferences ->
            // Lee el string de las preferencias, si no existe, usa GUINDA
            val themeName = preferences[THEME_KEY] ?: AppThemeType.GUINDA.name
            AppThemeType.valueOf(themeName)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppThemeType.GUINDA // Valor inicial mientras carga
        )

    /**
     * Guarda el tema seleccionado por el usuario en DataStore.
     */
    fun setTheme(theme: AppThemeType) {
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { preferences ->
                preferences[THEME_KEY] = theme.name
            }
        }
    }
}
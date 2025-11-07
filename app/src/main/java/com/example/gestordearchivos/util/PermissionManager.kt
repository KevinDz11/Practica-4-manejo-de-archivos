package com.example.gestordearchivos.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings

object PermissionManager {

    /**
     * Comprueba si el permiso MANAGE_EXTERNAL_STORAGE está concedido.
     * En Android 11 (API 30) y superior, esto comprueba Environment.isExternalStorageManager().
     */
    fun isStoragePermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 (API 30) y superior
            Environment.isExternalStorageManager()
        } else {
            // En versiones anteriores a Android 11, este permiso no existe.
            // La lógica dependería de READ/WRITE_EXTERNAL_STORAGE,
            // pero dado que el targetSDK es 36, el enfoque moderno es el correcto.
            // Para simplificar, asumiremos que si es < R, no está concedido
            // ya que la app está pidiendo el permiso moderno.
            false
        }
    }

    /**
     * Crea un Intent para que el usuario vaya a la pantalla de configuración
     * y conceda el permiso MANAGE_EXTERNAL_STORAGE.
     */
    fun getStoragePermissionIntent(context: Context): Intent {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 (API 30) y superior
            try {
                // Intenta abrir la pantalla de configuración específica de la app
                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    addCategory("android.intent.category.DEFAULT")
                    data = Uri.parse(String.format("package:%s", context.packageName))
                }
            } catch (e: Exception) {
                // Fallback a la pantalla genérica si falla
                Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            }
        } else {
            // No debería llegar aquí si la lógica de comprobación es correcta,
            // pero es un fallback seguro.
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse(String.format("package:%s", context.packageName))
            }
        }
        return intent
    }
}
package com.example.gestordearchivos.util

import android.webkit.MimeTypeMap
import java.util.Locale

object MimeTypeHelper {

    /**
     * Obtiene el tipo MIME de un archivo basándose en su extensión.
     *
     * @param file El archivo del cual obtener el tipo MIME.
     * @return Un string de tipo MIME (ej. "image/jpeg", "text/plain")
     * o "application/octet-stream" como un genérico si no se encuentra.
     */
    fun getMimeType(file: java.io.File): String {
        // Obtener la extensión del archivo en minúsculas
        val extension = file.extension.lowercase(Locale.getDefault())

        if (extension.isNotEmpty()) {
            // Usar el MimeTypeMap de Android para obtener el tipo MIME
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            if (mimeType != null) {
                return mimeType
            }
        }

        // Fallback para tipos de archivo desconocidos o sin extensión
        return "application/octet-stream"
    }
}
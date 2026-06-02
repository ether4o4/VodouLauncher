package rocks.gorjan.fourgauteauos.filemanager.model

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * FileItem — Data class representing a filesystem entry for the file manager.
 */
data class FileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val sizeBytes: Long,
    val lastModified: Long,
    val extension: String = ""
) {
    val formattedSize: String
        get() = when {
            isDirectory -> "—"
            sizeBytes < 1024 -> "$sizeBytes B"
            sizeBytes < 1024 * 1024 -> "%.1f KB".format(sizeBytes / 1024.0)
            sizeBytes < 1024 * 1024 * 1024 -> "%.1f MB".format(sizeBytes / (1024.0 * 1024.0))
            else -> "%.2f GB".format(sizeBytes / (1024.0 * 1024.0 * 1024.0))
        }

    val lastModifiedFormatted: String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            return sdf.format(Date(lastModified))
        }

    companion object {
        fun fromFile(file: File): FileItem = FileItem(
            name = file.name,
            path = file.absolutePath,
            isDirectory = file.isDirectory,
            sizeBytes = if (file.isFile) file.length() else 0L,
            lastModified = file.lastModified(),
            extension = file.extension.lowercase()
        )
    }
}

package com.edgebit.photoedge.data.storage

import android.content.Context
import com.edgebit.photoedge.data.model.MediaType
import com.edgebit.photoedge.domain.util.MediaFileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.util.UUID

class MediaStorageManager(private val context: Context) {

    private val mediaDirectory: File
        get() = File(context.filesDir, "media").apply {
            if (!exists()) mkdirs()
        }

    suspend fun saveMediaFile(
        originalFileName: String,
        inputStream: InputStream,
        mimeType: String
    ): SavedFileInfo = withContext(Dispatchers.IO) {
        val extension = originalFileName.substringAfterLast('.', "")
        val safeExtension = if (extension.isNotEmpty()) ".$extension" else ""
        val uniqueFileName = "${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}$safeExtension"
        val destinationFile = File(mediaDirectory, uniqueFileName)

        destinationFile.outputStream().use { output ->
            inputStream.copyTo(output)
        }

        val mediaType = if (MediaFileUtils.isVideoFile(originalFileName)) MediaType.VIDEO else MediaType.PHOTO

        SavedFileInfo(
            fileName = originalFileName,
            filePath = destinationFile.absolutePath,
            sizeBytes = destinationFile.length(),
            mediaType = mediaType,
            mimeType = mimeType
        )
    }

    suspend fun deleteFile(filePath: String): Boolean = withContext(Dispatchers.IO) {
        val file = File(filePath)
        if (file.exists()) file.delete() else false
    }

    suspend fun clearAllMediaFiles(): Boolean = withContext(Dispatchers.IO) {
        val files = mediaDirectory.listFiles() ?: return@withContext true
        var allSuccess = true
        for (file in files) {
            if (!file.delete()) {
                allSuccess = false
            }
        }
        allSuccess
    }

    suspend fun getStorageUsageBytes(): Long = withContext(Dispatchers.IO) {
        val files = mediaDirectory.listFiles() ?: return@withContext 0L
        files.sumOf { it.length() }
    }

    data class SavedFileInfo(
        val fileName: String,
        val filePath: String,
        val sizeBytes: Long,
        val mediaType: MediaType,
        val mimeType: String
    )
}

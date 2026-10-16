package com.edgebit.photoedge.domain.util

object MediaFileUtils {

    private val imageExtensions = setOf("jpg", "jpeg", "png", "webp", "gif", "bmp", "heic", "heif")
    private val videoExtensions = setOf("mp4", "mkv", "webm", "mov", "avi", "m4v", "3gp")

    fun extensionOf(fileName: String): String = fileName.substringAfterLast('.', "").lowercase()

    fun detectMimeType(fileName: String): String = when (extensionOf(fileName)) {
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        "webp" -> "image/webp"
        "gif" -> "image/gif"
        "bmp" -> "image/bmp"
        "heic" -> "image/heic"
        "heif" -> "image/heif"
        "mp4" -> "video/mp4"
        "mkv" -> "video/x-matroska"
        "webm" -> "video/webm"
        "mov" -> "video/quicktime"
        "avi" -> "video/x-msvideo"
        "m4v" -> "video/x-m4v"
        "3gp" -> "video/3gpp"
        else -> "application/octet-stream"
    }

    fun isSupportedMediaFile(fileName: String): Boolean {
        val ext = extensionOf(fileName)
        return ext in imageExtensions || ext in videoExtensions
    }

    fun isVideoFile(fileName: String): Boolean = extensionOf(fileName) in videoExtensions
}

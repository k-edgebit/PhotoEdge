package com.edgebit.photoedge.data.model

data class MediaItem(
    val id: Long = 0,
    val fileName: String,
    val filePath: String,
    val mediaType: MediaType = MediaType.PHOTO,
    val mimeType: String = "image/jpeg",
    val sizeBytes: Long = 0L,
    val durationMs: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val displayOrder: Int = 0
)

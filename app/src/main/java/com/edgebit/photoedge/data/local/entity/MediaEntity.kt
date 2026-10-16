package com.edgebit.photoedge.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.edgebit.photoedge.data.model.MediaItem
import com.edgebit.photoedge.data.model.MediaType

@Entity(tableName = "media_items")
data class MediaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fileName: String,
    val filePath: String,
    val mediaType: String,
    val mimeType: String,
    val sizeBytes: Long,
    val durationMs: Long,
    val createdAt: Long,
    val displayOrder: Int
) {
    fun toDomain(): MediaItem = MediaItem(
        id = id,
        fileName = fileName,
        filePath = filePath,
        mediaType = if (mediaType == "VIDEO") MediaType.VIDEO else MediaType.PHOTO,
        mimeType = mimeType,
        sizeBytes = sizeBytes,
        durationMs = durationMs,
        createdAt = createdAt,
        displayOrder = displayOrder
    )

    companion object {
        fun fromDomain(item: MediaItem): MediaEntity = MediaEntity(
            id = item.id,
            fileName = item.fileName,
            filePath = item.filePath,
            mediaType = item.mediaType.name,
            mimeType = item.mimeType,
            sizeBytes = item.sizeBytes,
            durationMs = item.durationMs,
            createdAt = item.createdAt,
            displayOrder = item.displayOrder
        )
    }
}

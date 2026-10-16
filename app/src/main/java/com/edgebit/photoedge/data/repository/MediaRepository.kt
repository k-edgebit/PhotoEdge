package com.edgebit.photoedge.data.repository

import com.edgebit.photoedge.data.local.dao.MediaDao
import com.edgebit.photoedge.data.local.entity.MediaEntity
import com.edgebit.photoedge.data.model.MediaItem
import com.edgebit.photoedge.data.storage.MediaStorageManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.InputStream

class MediaRepository(
    private val mediaDao: MediaDao,
    private val storageManager: MediaStorageManager
) {
    val allMedia: Flow<List<MediaItem>> = mediaDao.getAllMedia().map { entities ->
        entities.map { it.toDomain() }
    }

    val mediaCount: Flow<Int> = mediaDao.getMediaCount()

    suspend fun getAllMediaList(): List<MediaItem> {
        return mediaDao.getAllMediaList().map { it.toDomain() }
    }

    suspend fun addMedia(
        fileName: String,
        inputStream: InputStream,
        mimeType: String
    ): MediaItem {
        val saved = storageManager.saveMediaFile(fileName, inputStream, mimeType)
        val entity = MediaEntity(
            fileName = saved.fileName,
            filePath = saved.filePath,
            mediaType = saved.mediaType.name,
            mimeType = saved.mimeType,
            sizeBytes = saved.sizeBytes,
            durationMs = 0L,
            createdAt = System.currentTimeMillis(),
            displayOrder = 0
        )
        val id = mediaDao.insertMedia(entity)
        return entity.copy(id = id).toDomain()
    }

    suspend fun deleteMedia(item: MediaItem) {
        storageManager.deleteFile(item.filePath)
        mediaDao.deleteById(item.id)
    }

    suspend fun clearAll() {
        storageManager.clearAllMediaFiles()
        mediaDao.clearAll()
    }

    suspend fun getStorageUsageBytes(): Long {
        return storageManager.getStorageUsageBytes()
    }
}

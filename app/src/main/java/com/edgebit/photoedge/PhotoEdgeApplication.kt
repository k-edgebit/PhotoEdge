package com.edgebit.photoedge

import android.app.Application
import android.util.Log
import com.edgebit.photoedge.data.local.AppDatabase
import com.edgebit.photoedge.data.local.PreferencesManager
import com.edgebit.photoedge.data.repository.MediaRepository
import com.edgebit.photoedge.data.repository.SettingsRepository
import com.edgebit.photoedge.data.server.PhotoEdgeServer
import com.edgebit.photoedge.data.storage.MediaStorageManager
import com.edgebit.photoedge.domain.util.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PhotoEdgeApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var storageManager: MediaStorageManager
        private set

    lateinit var mediaRepository: MediaRepository
        private set

    lateinit var preferencesManager: PreferencesManager
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    lateinit var alarmScheduler: AlarmScheduler
        private set

    private var httpServer: PhotoEdgeServer? = null

    private val _mediaUploadEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val mediaUploadEvents: SharedFlow<Unit> = _mediaUploadEvents

    private val appScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = AppDatabase.getInstance(this)
        storageManager = MediaStorageManager(this)
        mediaRepository = MediaRepository(database.mediaDao(), storageManager)
        preferencesManager = PreferencesManager(this)
        settingsRepository = SettingsRepository(preferencesManager)
        alarmScheduler = AlarmScheduler(this)

        startLocalHttpServer()
    }

    fun startLocalHttpServer() {
        appScope.launch(Dispatchers.IO) {
            try {
                if (httpServer == null) {
                    val settings = settingsRepository.settings.first()
                    httpServer = PhotoEdgeServer(
                        context = this@PhotoEdgeApplication,
                        mediaRepository = mediaRepository,
                        getSettings = { settingsRepository.settings.first() },
                        onMediaUploaded = {
                            _mediaUploadEvents.tryEmit(Unit)
                        },
                        port = settings.serverPort
                    )
                    httpServer?.start()
                    Log.d("PhotoEdgeApp", "Local HTTP server started on port ${settings.serverPort}")
                }
            } catch (e: Exception) {
                Log.e("PhotoEdgeApp", "Failed to start local HTTP server", e)
            }
        }
    }

    fun stopLocalHttpServer() {
        httpServer?.stop()
        httpServer = null
        Log.d("PhotoEdgeApp", "Local HTTP server stopped")
    }

    companion object {
        lateinit var instance: PhotoEdgeApplication
            private set
    }
}

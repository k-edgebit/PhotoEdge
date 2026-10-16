package com.edgebit.photoedge.ui.slideshow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edgebit.photoedge.PhotoEdgeApplication
import com.edgebit.photoedge.data.model.MediaItem
import com.edgebit.photoedge.data.model.MediaType
import com.edgebit.photoedge.data.model.TransitionEffect
import com.edgebit.photoedge.data.repository.MediaRepository
import com.edgebit.photoedge.data.repository.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SlideshowUiState(
    val mediaList: List<MediaItem> = emptyList(),
    val currentIndex: Int = 0,
    val isPlaying: Boolean = true,
    val transitionEffect: TransitionEffect = TransitionEffect.FADE,
    val intervalSeconds: Int = 10,
    val isMuted: Boolean = false,
    val keepScreenOn: Boolean = true,
    val isControlsVisible: Boolean = false
) {
    val currentItem: MediaItem?
        get() = if (mediaList.isNotEmpty() && currentIndex in mediaList.indices) {
            mediaList[currentIndex]
        } else null
}

class SlideshowViewModel(
    private val mediaRepository: MediaRepository = PhotoEdgeApplication.instance.mediaRepository,
    private val settingsRepository: SettingsRepository = PhotoEdgeApplication.instance.settingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SlideshowUiState())
    val uiState: StateFlow<SlideshowUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var controlsTimerJob: Job? = null

    init {
        // Observe settings
        viewModelScope.launch {
            settingsRepository.settings.collectLatest { settings ->
                _uiState.update {
                    it.copy(
                        transitionEffect = settings.transitionEffect,
                        intervalSeconds = settings.slideshowIntervalSeconds,
                        isMuted = settings.isMuted,
                        keepScreenOn = settings.keepScreenOn
                    )
                }
                restartTimer()
            }
        }

        // Observe media list
        viewModelScope.launch {
            mediaRepository.allMedia.collectLatest { list ->
                _uiState.update { current ->
                    val newIndex = if (current.currentIndex >= list.size) 0 else current.currentIndex
                    current.copy(mediaList = list, currentIndex = newIndex)
                }
                restartTimer()
            }
        }

        // Listen for new PC uploads
        viewModelScope.launch {
            PhotoEdgeApplication.instance.mediaUploadEvents.collectLatest {
                val list = mediaRepository.getAllMediaList()
                _uiState.update { current ->
                    current.copy(mediaList = list, currentIndex = 0)
                }
                restartTimer()
            }
        }
    }

    fun next() {
        val size = _uiState.value.mediaList.size
        if (size > 0) {
            _uiState.update { it.copy(currentIndex = (it.currentIndex + 1) % size) }
            restartTimer()
        }
    }

    fun previous() {
        val size = _uiState.value.mediaList.size
        if (size > 0) {
            _uiState.update { it.copy(currentIndex = if (it.currentIndex - 1 < 0) size - 1 else it.currentIndex - 1) }
            restartTimer()
        }
    }

    fun togglePlay() {
        val playing = !_uiState.value.isPlaying
        _uiState.update { it.copy(isPlaying = playing) }
        if (playing) {
            restartTimer()
        } else {
            timerJob?.cancel()
        }
    }

    fun onVideoFinished() {
        next()
    }

    fun toggleControls() {
        val newVisible = !_uiState.value.isControlsVisible
        _uiState.update { it.copy(isControlsVisible = newVisible) }
        controlsTimerJob?.cancel()
        if (newVisible) {
            controlsTimerJob = viewModelScope.launch {
                delay(4000)
                _uiState.update { it.copy(isControlsVisible = false) }
            }
        }
    }

    private fun restartTimer() {
        timerJob?.cancel()
        val currentItem = _uiState.value.currentItem
        // Don't auto timer for videos; video completion will trigger next()
        if (_uiState.value.isPlaying && currentItem?.mediaType != MediaType.VIDEO) {
            timerJob = viewModelScope.launch {
                delay(_uiState.value.intervalSeconds * 1000L)
                next()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        controlsTimerJob?.cancel()
    }
}

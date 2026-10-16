package com.edgebit.photoedge.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edgebit.photoedge.PhotoEdgeApplication
import com.edgebit.photoedge.data.model.FrameSettings
import com.edgebit.photoedge.data.model.TransitionEffect
import com.edgebit.photoedge.data.repository.MediaRepository
import com.edgebit.photoedge.data.repository.SettingsRepository
import com.edgebit.photoedge.domain.util.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val settings: FrameSettings = FrameSettings(),
    val storageUsageText: String = "계산 중...",
    val mediaCount: Int = 0
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository = PhotoEdgeApplication.instance.settingsRepository,
    private val mediaRepository: MediaRepository = PhotoEdgeApplication.instance.mediaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collectLatest { s ->
                _uiState.update { it.copy(settings = s) }
            }
        }

        viewModelScope.launch {
            mediaRepository.mediaCount.collectLatest { count ->
                _uiState.update { it.copy(mediaCount = count) }
                updateStorageUsage()
            }
        }

        updateStorageUsage()
    }

    fun updateStorageUsage() {
        viewModelScope.launch {
            val bytes = mediaRepository.getStorageUsageBytes()
            _uiState.update { it.copy(storageUsageText = NetworkUtils.formatBytes(bytes)) }
        }
    }

    fun setSlideshowInterval(seconds: Int) {
        viewModelScope.launch {
            settingsRepository.updateSlideshowInterval(seconds)
        }
    }

    fun setTransitionEffect(effect: TransitionEffect) {
        viewModelScope.launch {
            settingsRepository.updateTransitionEffect(effect)
        }
    }

    fun setKeepScreenOn(keepOn: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateKeepScreenOn(keepOn)
        }
    }

    fun setIsMuted(muted: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateIsMuted(muted)
        }
    }

    fun setSleepMode(enabled: Boolean, startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) {
        viewModelScope.launch {
            settingsRepository.updateSleepMode(enabled, startHour, startMinute, endHour, endMinute)
        }
    }

    fun clearAllMedia() {
        viewModelScope.launch {
            mediaRepository.clearAll()
            updateStorageUsage()
        }
    }
}

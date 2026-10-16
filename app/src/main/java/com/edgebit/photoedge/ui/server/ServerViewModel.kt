package com.edgebit.photoedge.ui.server

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edgebit.photoedge.PhotoEdgeApplication
import com.edgebit.photoedge.data.repository.MediaRepository
import com.edgebit.photoedge.data.repository.SettingsRepository
import com.edgebit.photoedge.domain.util.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ServerStatusUiState(
    val ipAddress: String? = null,
    val port: Int = 8080,
    val pinCode: String = "1234",
    val isServerRunning: Boolean = true,
    val mediaCount: Int = 0
) {
    val serverUrl: String
        get() = if (ipAddress != null) "http://$ipAddress:$port" else "Wi-Fi에 연결되어 있지 않습니다"
}

class ServerViewModel(
    private val settingsRepository: SettingsRepository = PhotoEdgeApplication.instance.settingsRepository,
    private val mediaRepository: MediaRepository = PhotoEdgeApplication.instance.mediaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServerStatusUiState())
    val uiState: StateFlow<ServerStatusUiState> = _uiState.asStateFlow()

    init {
        refreshNetworkInfo()

        viewModelScope.launch {
            settingsRepository.settings.collectLatest { settings ->
                _uiState.update {
                    it.copy(
                        port = settings.serverPort,
                        pinCode = settings.serverPinCode
                    )
                }
            }
        }

        viewModelScope.launch {
            mediaRepository.mediaCount.collectLatest { count ->
                _uiState.update { it.copy(mediaCount = count) }
            }
        }
    }

    fun refreshNetworkInfo() {
        val ip = NetworkUtils.getLocalIpAddress()
        _uiState.update { it.copy(ipAddress = ip) }
    }

    fun toggleServer() {
        val willRun = !_uiState.value.isServerRunning
        if (willRun) {
            PhotoEdgeApplication.instance.startLocalHttpServer()
        } else {
            PhotoEdgeApplication.instance.stopLocalHttpServer()
        }
        _uiState.update { it.copy(isServerRunning = willRun) }
    }
}

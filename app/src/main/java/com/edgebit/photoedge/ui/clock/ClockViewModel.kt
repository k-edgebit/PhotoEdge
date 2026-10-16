package com.edgebit.photoedge.ui.clock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edgebit.photoedge.PhotoEdgeApplication
import com.edgebit.photoedge.data.repository.SettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ClockUiState(
    val currentTimeText: String = "",
    val currentSecondText: String = "",
    val currentDateText: String = "",
    val currentDayOfWeekText: String = "",
    val isSleepModeActive: Boolean = false,
    val sleepModeEnabled: Boolean = false
)

class ClockViewModel(
    private val settingsRepository: SettingsRepository = PhotoEdgeApplication.instance.settingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClockUiState())
    val uiState: StateFlow<ClockUiState> = _uiState.asStateFlow()

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val secondFormat = SimpleDateFormat("ss", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.KOREA)
    private val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.KOREA)

    init {
        // Clock tick ticker
        viewModelScope.launch {
            while (true) {
                val now = Date()
                _uiState.update {
                    it.copy(
                        currentTimeText = timeFormat.format(now),
                        currentSecondText = secondFormat.format(now),
                        currentDateText = dateFormat.format(now),
                        currentDayOfWeekText = dayOfWeekFormat.format(now)
                    )
                }
                delay(1000)
            }
        }

        // Settings observe
        viewModelScope.launch {
            settingsRepository.settings.collectLatest { settings ->
                _uiState.update {
                    it.copy(sleepModeEnabled = settings.sleepModeEnabled)
                }
            }
        }
    }

    fun toggleSleepMode() {
        _uiState.update { it.copy(isSleepModeActive = !it.isSleepModeActive) }
    }
}

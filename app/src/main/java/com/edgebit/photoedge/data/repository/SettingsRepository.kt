package com.edgebit.photoedge.data.repository

import com.edgebit.photoedge.data.local.PreferencesManager
import com.edgebit.photoedge.data.model.FrameSettings
import com.edgebit.photoedge.data.model.TransitionEffect
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val preferencesManager: PreferencesManager) {

    val settings: Flow<FrameSettings> = preferencesManager.settingsFlow

    suspend fun updateSlideshowInterval(seconds: Int) {
        preferencesManager.updateSlideshowInterval(seconds)
    }

    suspend fun updateTransitionEffect(effect: TransitionEffect) {
        preferencesManager.updateTransitionEffect(effect)
    }

    suspend fun updateKeepScreenOn(keepOn: Boolean) {
        preferencesManager.updateKeepScreenOn(keepOn)
    }

    suspend fun updateIsMuted(muted: Boolean) {
        preferencesManager.updateIsMuted(muted)
    }

    suspend fun updateSleepMode(enabled: Boolean, startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) {
        preferencesManager.updateSleepMode(enabled, startHour, startMinute, endHour, endMinute)
    }

    suspend fun updateServerPin(pin: String) {
        preferencesManager.updateServerPin(pin)
    }

    suspend fun updateServerPort(port: Int) {
        preferencesManager.updateServerPort(port)
    }
}

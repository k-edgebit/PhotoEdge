package com.edgebit.photoedge.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.edgebit.photoedge.data.model.FrameSettings
import com.edgebit.photoedge.data.model.TransitionEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "frame_settings")

class PreferencesManager(private val context: Context) {

    private object PreferencesKeys {
        val SLIDESHOW_INTERVAL = intPreferencesKey("slideshow_interval_seconds")
        val TRANSITION_EFFECT = stringPreferencesKey("transition_effect")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        val IS_MUTED = booleanPreferencesKey("is_muted")
        val SLEEP_MODE_ENABLED = booleanPreferencesKey("sleep_mode_enabled")
        val SLEEP_START_HOUR = intPreferencesKey("sleep_start_hour")
        val SLEEP_START_MINUTE = intPreferencesKey("sleep_start_minute")
        val SLEEP_END_HOUR = intPreferencesKey("sleep_end_hour")
        val SLEEP_END_MINUTE = intPreferencesKey("sleep_end_minute")
        val SERVER_PORT = intPreferencesKey("server_port")
        val SERVER_PIN_CODE = stringPreferencesKey("server_pin_code")
    }

    val settingsFlow: Flow<FrameSettings> = context.dataStore.data.map { preferences ->
        val interval = preferences[PreferencesKeys.SLIDESHOW_INTERVAL] ?: 10
        val effectName = preferences[PreferencesKeys.TRANSITION_EFFECT] ?: TransitionEffect.FADE.name
        val transitionEffect = try {
            TransitionEffect.valueOf(effectName)
        } catch (e: Exception) {
            TransitionEffect.FADE
        }
        val keepScreenOn = preferences[PreferencesKeys.KEEP_SCREEN_ON] ?: true
        val isMuted = preferences[PreferencesKeys.IS_MUTED] ?: false
        val sleepEnabled = preferences[PreferencesKeys.SLEEP_MODE_ENABLED] ?: false
        val sleepStartH = preferences[PreferencesKeys.SLEEP_START_HOUR] ?: 23
        val sleepStartM = preferences[PreferencesKeys.SLEEP_START_MINUTE] ?: 0
        val sleepEndH = preferences[PreferencesKeys.SLEEP_END_HOUR] ?: 7
        val sleepEndM = preferences[PreferencesKeys.SLEEP_END_MINUTE] ?: 0
        val port = preferences[PreferencesKeys.SERVER_PORT] ?: 8080
        val pin = preferences[PreferencesKeys.SERVER_PIN_CODE] ?: "1234"

        FrameSettings(
            slideshowIntervalSeconds = interval,
            transitionEffect = transitionEffect,
            keepScreenOn = keepScreenOn,
            isMuted = isMuted,
            sleepModeEnabled = sleepEnabled,
            sleepStartHour = sleepStartH,
            sleepStartMinute = sleepStartM,
            sleepEndHour = sleepEndH,
            sleepEndMinute = sleepEndM,
            serverPort = port,
            serverPinCode = pin
        )
    }

    suspend fun updateSlideshowInterval(seconds: Int) {
        context.dataStore.edit { it[PreferencesKeys.SLIDESHOW_INTERVAL] = seconds }
    }

    suspend fun updateTransitionEffect(effect: TransitionEffect) {
        context.dataStore.edit { it[PreferencesKeys.TRANSITION_EFFECT] = effect.name }
    }

    suspend fun updateKeepScreenOn(keepOn: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.KEEP_SCREEN_ON] = keepOn }
    }

    suspend fun updateIsMuted(muted: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.IS_MUTED] = muted }
    }

    suspend fun updateSleepMode(enabled: Boolean, startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) {
        context.dataStore.edit {
            it[PreferencesKeys.SLEEP_MODE_ENABLED] = enabled
            it[PreferencesKeys.SLEEP_START_HOUR] = startHour
            it[PreferencesKeys.SLEEP_START_MINUTE] = startMinute
            it[PreferencesKeys.SLEEP_END_HOUR] = endHour
            it[PreferencesKeys.SLEEP_END_MINUTE] = endMinute
        }
    }

    suspend fun updateServerPin(pin: String) {
        context.dataStore.edit { it[PreferencesKeys.SERVER_PIN_CODE] = pin }
    }

    suspend fun updateServerPort(port: Int) {
        context.dataStore.edit { it[PreferencesKeys.SERVER_PORT] = port }
    }
}

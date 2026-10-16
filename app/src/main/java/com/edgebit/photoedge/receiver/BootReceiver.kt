package com.edgebit.photoedge.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.edgebit.photoedge.PhotoEdgeApplication
import com.edgebit.photoedge.domain.util.AlarmScheduler
import kotlinx.coroutines.flow.first

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        Log.d("BootReceiver", "Device reboot completed, restoring PhotoEdge alarms...")

        val app = PhotoEdgeApplication.instance
        val scheduler = AlarmScheduler(context)

        val settings = runCatching {
            kotlinx.coroutines.runBlocking { app.settingsRepository.settings.first() }
        }.getOrNull() ?: return

        if (!settings.sleepModeEnabled) return

        scheduler.scheduleSleepAlarm(settings.sleepStartHour, settings.sleepStartMinute, isSleepStart = true)
        scheduler.scheduleSleepAlarm(settings.sleepEndHour, settings.sleepEndMinute, isSleepStart = false)
    }
}

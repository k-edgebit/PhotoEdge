package com.edgebit.photoedge.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d("AlarmReceiver", "Received action: $action")
        when (action) {
            ACTION_SLEEP_START -> {
                Log.d("AlarmReceiver", "Sleep mode triggered (start sleep)")
            }
            ACTION_SLEEP_END -> {
                Log.d("AlarmReceiver", "Wake up mode triggered (end sleep)")
            }
        }
    }

    companion object {
        const val ACTION_SLEEP_START = "com.edgebit.photoedge.ACTION_SLEEP_START"
        const val ACTION_SLEEP_END = "com.edgebit.photoedge.ACTION_SLEEP_END"
    }
}

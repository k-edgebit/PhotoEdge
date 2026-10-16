package com.edgebit.photoedge.data.model

data class FrameSettings(
    val slideshowIntervalSeconds: Int = 10,
    val transitionEffect: TransitionEffect = TransitionEffect.FADE,
    val keepScreenOn: Boolean = true,
    val isMuted: Boolean = false,
    val sleepModeEnabled: Boolean = false,
    val sleepStartHour: Int = 23,
    val sleepStartMinute: Int = 0,
    val sleepEndHour: Int = 7,
    val sleepEndMinute: Int = 0,
    val serverPort: Int = 8080,
    val serverPinCode: String = "1234"
)

package com.edgebit.photoedge.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Slideshow : Screen("slideshow", "전자액자")
    object Clock : Screen("clock", "시계 & 탁상")
    object ServerStatus : Screen("server_status", "PC 업로드")
    object Settings : Screen("settings", "설정")
}

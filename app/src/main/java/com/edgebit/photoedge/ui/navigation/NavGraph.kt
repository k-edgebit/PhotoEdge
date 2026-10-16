package com.edgebit.photoedge.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.edgebit.photoedge.ui.clock.ClockScreen
import com.edgebit.photoedge.ui.server.ServerStatusScreen
import com.edgebit.photoedge.ui.settings.SettingsScreen
import com.edgebit.photoedge.ui.slideshow.SlideshowScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Slideshow.route
    ) {
        composable(Screen.Slideshow.route) {
            SlideshowScreen(
                onNavigateToServer = { navController.navigate(Screen.ServerStatus.route) },
                onNavigateToClock = { navController.navigate(Screen.Clock.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Clock.route) {
            ClockScreen(
                onBackToSlideshow = { navController.popBackStack() }
            )
        }

        composable(Screen.ServerStatus.route) {
            ServerStatusScreen(
                onBackToSlideshow = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackToSlideshow = { navController.popBackStack() }
            )
        }
    }
}

package com.mehmettekin.gunkurasiapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mehmettekin.gunkurasiapp.presentation.screens.cark.CarkScreen
import com.mehmettekin.gunkurasiapp.presentation.screens.giris.GirisScreen
import com.mehmettekin.gunkurasiapp.presentation.screens.kapalicarsi.KapalicarsiScreen
import com.mehmettekin.gunkurasiapp.presentation.screens.settings.SettingsScreen
import com.mehmettekin.gunkurasiapp.presentation.screens.sonuc.SonucScreen
import com.mehmettekin.gunkurasiapp.presentation.screens.splash.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(route = Screen.Giris.route) {
            GirisScreen(navController = navController)
        }

        composable(route = Screen.Cark.route) {
            CarkScreen(navController = navController)
        }

        composable(route = Screen.Sonuc.route) {
            SonucScreen(navController = navController)
        }

        composable(route = Screen.Kapalicarsi.route) {
            KapalicarsiScreen()
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
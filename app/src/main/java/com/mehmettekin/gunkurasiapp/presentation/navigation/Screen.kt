package com.mehmettekin.gunkurasiapp.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash_screen")
    data object Giris : Screen("giris_screen")
    data object Cark : Screen("cark_screen")
    data object Sonuc : Screen("sonuc_screen")
    data object Kapalicarsi : Screen("kapalicarsi_screen")
    data object Settings : Screen("settings_screen")
}
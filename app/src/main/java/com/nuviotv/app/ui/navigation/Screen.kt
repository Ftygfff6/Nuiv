package com.nuviotv.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Screen("home", "الرئيسية", Icons.Default.Home)
    data object Explore : Screen("explore", "استكشف", Icons.Default.Explore)
    data object Library : Screen("library", "مكتبتي", Icons.Default.VideoLibrary)
    data object Search : Screen("search", "بحث", Icons.Default.Search)
    data object Settings : Screen("settings", "إعدادات", Icons.Default.Settings)
    companion object { val items = listOf(Home, Explore, Library, Search, Settings) }
}

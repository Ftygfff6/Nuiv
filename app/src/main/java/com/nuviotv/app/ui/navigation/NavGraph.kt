package com.nuviotv.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nuviotv.app.ui.screens.explore.ExploreScreen
import com.nuviotv.app.ui.screens.home.HomeScreen
import com.nuviotv.app.ui.screens.library.LibraryScreen
import com.nuviotv.app.ui.screens.search.SearchScreen
import com.nuviotv.app.ui.screens.settings.SettingsScreen

@Composable
fun NiovNavGraph() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(nav) }
        composable(Screen.Explore.route) { ExploreScreen(nav) }
        composable(Screen.Library.route) { LibraryScreen(nav) }
        composable(Screen.Search.route) { SearchScreen(nav) }
        composable(Screen.Settings.route) { SettingsScreen(nav) }
    }
}

package com.nuviotv.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nuviotv.app.ui.screens.addons.AddAddonScreen
import com.nuviotv.app.ui.screens.addons.AddonsListScreen
import com.nuviotv.app.ui.screens.home.HomeScreen
import com.nuviotv.app.ui.screens.settings.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF7B5CFF),
                    background = Color(0xFF0E0E12),
                    surface = Color(0xFF16161D),
                    onPrimary = Color.White,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0E0E12)
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(Modifier.fillMaxSize().background(Color(0xFF0E0E12))) {
        // Sidebar only on main screens
        if (currentRoute !in listOf("add_addon", "addons", "player")) {
            Sidebar(navController, currentRoute)
        }
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.weight(1f)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("explore") { HomeScreen(navController) }
            composable("library") { HomeScreen(navController) }
            composable("search") { HomeScreen(navController) }
            composable("settings") { SettingsScreen(navController) }
            composable("addons") { AddonsListScreen(navController) }
            composable("add_addon") { AddAddonScreen(navController) }
        }
    }
}

data class SidebarItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun Sidebar(navController: NavController, currentRoute: String?) {
    val menuItems = listOf(
        SidebarItem("الرئيسية", Icons.Default.Home, "home"),
        SidebarItem("استكشف", Icons.Default.Explore, "explore"),
        SidebarItem("مكتبتي", Icons.Default.VideoLibrary, "library"),
        SidebarItem("بحث", Icons.Default.Search, "search"),
        SidebarItem("الإعدادات", Icons.Default.Settings, "settings")
    )

    Column(
        modifier = Modifier
            .width(120.dp)
            .fillMaxHeight()
            .background(Color(0xFF0A0A0F))
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Niov",
            color = Color(0xFF7B5CFF),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        menuItems.forEach { item ->
            SidebarButton(
                item = item,
                isSelected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun SidebarButton(
    item: SidebarItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(72.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF7B5CFF).copy(alpha = 0.18f) else Color.Transparent,
            contentColor = if (isSelected) Color(0xFF7B5CFF) else Color(0xFFB0B0BE)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(item.icon, item.label, tint = if (isSelected) Color(0xFF7B5CFF) else Color(0xFFB0B0BE))
            Spacer(Modifier.height(4.dp))
            Text(
                item.label,
                fontSize = 11.sp,
                color = if (isSelected) Color(0xFF7B5CFF) else Color(0xFFB0B0BE)
            )
        }
    }
}

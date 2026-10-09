package com.nuviotv.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
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
import androidx.tv.material3.Card
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text as TvText
import androidx.tv.material3.darkColorScheme

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
                    modifier = Modifier.fillMaxSize().background(Color(0xFF0E0E12)),
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

    Row(Modifier.fillMaxSize()) {
        Sidebar(navController, currentRoute)
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.weight(1f)
        ) {
            composable("home") { HomeScreen() }
            composable("explore") { PlaceholderScreen("استكشف") }
            composable("library") { PlaceholderScreen("مكتبتي") }
            composable("search") { PlaceholderScreen("البحث") }
            composable("settings") { PlaceholderScreen("الإعدادات") }
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
        SidebarItem("إعدادات", Icons.Default.Settings, "settings")
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
        TvText(
            text = "Niov",
            color = Color(0xFF7B5CFF),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        menuItems.forEach { item ->
            Card(
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).height(72.dp),
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = if (currentRoute == item.route) Color(0xFF7B5CFF).copy(alpha = 0.18f) else Color.Transparent,
                    focusedContainerColor = Color(0xFF7B5CFF).copy(alpha = 0.35f)
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (currentRoute == item.route) Color(0xFF7B5CFF) else Color(0xFFB0B0BE)
                    )
                    Spacer(Modifier.height(4.dp))
                    TvText(
                        text = item.label,
                        fontSize = 11.sp,
                        color = if (currentRoute == item.route) Color(0xFF7B5CFF) else Color(0xFFB0B0BE)
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(300.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            TvText(
                text = "مرحبًا بك في Niov",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        MediaRow("Continue Watching")
        MediaRow("Trending Now")
        MediaRow("Your Addons")
    }
}

@Composable
fun MediaRow(title: String) {
    Column {
        TvText(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(5) { index ->
                Card(
                    onClick = { },
                    modifier = Modifier.width(200.dp).height(280.dp),
                    colors = ClickableSurfaceDefaults.colors(containerColor = Color(0xFF16161D))
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        TvText("Media ${index + 1}", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        TvText(text = title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

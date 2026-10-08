package com.nuviotv.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.tv.material3.*
import com.nuviotv.app.ui.navigation.Screen
import com.nuviotv.app.ui.theme.NiovPrimary
import com.nuviotv.app.ui.theme.NiovTextMuted

@Composable
fun Sidebar(navController: NavController) {
    val entry by navController.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    Column(
        Modifier.fillMaxHeight().width(110.dp).background(Color(0xFF0A0A0F)).padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Niov", color = NiovPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))
        Screen.items.forEach { s ->
            Card(
                onClick = { if (route != s.route) navController.navigate(s.route) { popUpTo(Screen.Home.route) { saveState = true }; launchSingleTop = true; restoreState = true } },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).height(72.dp),
                shape = ClickableSurfaceDefaults.shape(shape = RoundedCornerShape(12.dp)),
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = if (route == s.route) NiovPrimary.copy(alpha = 0.18f) else Color.Transparent,
                    focusedContainerColor = NiovPrimary.copy(alpha = 0.35f)
                ),
                scale = ClickableSurfaceDefaults.scale(focusedScale = 1.05f)
            ) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(s.icon, s.label, tint = if (route == s.route) NiovPrimary else NiovTextMuted)
                    Spacer(Modifier.height(4.dp))
                    Text(s.label, fontSize = 11.sp, color = if (route == s.route) NiovPrimary else NiovTextMuted)
                }
            }
        }
    }
}

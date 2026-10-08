package com.nuviotv.app.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.tv.material3.Text
import com.nuviotv.app.ui.components.Sidebar
import com.nuviotv.app.ui.theme.NiovBackground

@Composable
fun LibraryScreen(navController: NavController) {
    Row(Modifier.fillMaxSize().background(NiovBackground)) {
        Sidebar(navController)
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("مكتبتي — قريبًا", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

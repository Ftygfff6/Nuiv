package com.nuviotv.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E12))
    ) {
        // Hero
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF2A1F5C), Color(0xFF0E0E12)))),
            contentAlignment = Alignment.BottomStart
        ) {
            Column(Modifier.padding(32.dp)) {
                Text(
                    "مرحبًا بك في Niov",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "ابدأ بإضافة إضافة (Addon) لعرض المحتوى",
                    fontSize = 16.sp,
                    color = Color(0xFFB0B0BE)
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("addons") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B5CFF)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Extension, null)
                    Spacer(Modifier.width(8.dp))
                    Text("إدارة الإضافات", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Info cards
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoCard(
                title = "إضافات مقترحة",
                desc = "Cinemeta · Torrentio · OpenSubtitles",
                icon = Icons.Default.Extension,
                onClick = { navController.navigate("addons") },
                modifier = Modifier.weight(1f)
            )
            InfoCard(
                title = "الإعدادات",
                desc = "الترجمة · الجودة · المفضلة",
                icon = Icons.Default.Settings,
                onClick = { navController.navigate("settings") },
                modifier = Modifier.weight(1f)
            )
            InfoCard(
                title = "المفضلة",
                desc = "ما حفظته للمشاهدة لاحقًا",
                icon = Icons.Default.Favorite,
                onClick = { navController.navigate("library") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(140.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16161D)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = Color(0xFF7B5CFF), modifier = Modifier.size(32.dp))
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(desc, color = Color(0xFFB0B0BE), fontSize = 12.sp)
        }
    }
}

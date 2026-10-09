package com.nuviotv.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController) {
    var subtitleEnabled by remember { mutableStateOf(true) }
    var autoPlay by remember { mutableStateOf(false) }
    var quality by remember { mutableStateOf("Auto") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E12))
            .verticalScroll(rememberScrollState())
            .padding(32.dp)
    ) {
        Text(
            "الإعدادات",
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Section: Playback
        SettingsSection("التشغيل") {
            SettingsSwitch("تفعيل الترجمة افتراضيًا", subtitleEnabled) { subtitleEnabled = it }
            SettingsSwitch("التشغيل التلقائي للمصدر الأول", autoPlay) { autoPlay = it }
            SettingsDropdown(
                label = "جودة الفيديو",
                options = listOf("Auto", "1080p", "720p", "480p"),
                selected = quality,
                onSelected = { quality = it }
            )
        }

        Spacer(Modifier.height(16.dp))

        // Section: Content
        SettingsSection("المحتوى") {
            SettingsButton("إدارة الإضافات", Icons.Default.Extension) {
                navController.navigate("addons")
            }
            SettingsButton("المفضلة", Icons.Default.Favorite) {
                navController.navigate("library")
            }
            SettingsButton("سجل المشاهدة", Icons.Default.History) {
                // TODO: navigate to history
            }
        }

        Spacer(Modifier.height(16.dp))

        // Section: About
        SettingsSection("حول") {
            SettingsButton("حول Niov", Icons.Default.Info) { }
            SettingsButton("الشروط والأحكام", Icons.Default.Description) { }
            SettingsButton("الإصدار", Icons.Default.Info) { }
        }

        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF16161D), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF7B5CFF),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
private fun SettingsSwitch(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White, fontSize = 16.sp)
        Switch(checked = checked, onCheckedChange = onToggle)
    }
}

@Composable
private fun SettingsButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1F1F2A),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Color(0xFF7B5CFF))
            Spacer(Modifier.width(12.dp))
            Text(label, fontSize = 16.sp)
        }
    }
}

@Composable
private fun SettingsDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, color = Color(0xFFB0B0BE), fontSize = 14.sp)
        Spacer(Modifier.height(4.dp))
        Box {
            Button(
                onClick = { expanded = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F1F2A))
            ) {
                Text(selected, color = Color.White)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = { onSelected(option); expanded = false }
                    )
                }
            }
        }
    }
}

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
import com.nuviotv.app.di.AppContainer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var rdKey by remember { mutableStateOf("") }
    var torboxKey by remember { mutableStateOf("") }
    var premiumizeKey by remember { mutableStateOf("") }
    var metadataSource by remember { mutableStateOf("cinemeta") }
    var tmdbKey by remember { mutableStateOf("") }
    var statusMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        rdKey = AppContainer.debridPreferences.realDebridKey.first()
        torboxKey = AppContainer.debridPreferences.torboxKey.first()
        premiumizeKey = AppContainer.debridPreferences.premiumizeKey.first()
        metadataSource = AppContainer.debridPreferences.metadataSource.first()
        tmdbKey = AppContainer.debridPreferences.tmdbKey.first()
    }

    Column(
        Modifier.fillMaxSize().background(Color(0xFF0E0E12)).verticalScroll(rememberScrollState()).padding(32.dp)
    ) {
        Text("الإعدادات", fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(bottom = 24.dp))

        // Metadata
        SettingsSection("مصدر البيانات (Metadata)") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = metadataSource == "cinemeta",
                    onClick = { metadataSource = "cinemeta"; scope.launch { AppContainer.debridPreferences.setMetadataSource("cinemeta") } }
                )
                Text("Cinemeta (عام — بدون مفتاح)", color = Color.White)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = metadataSource == "tmdb",
                    onClick = { metadataSource = "tmdb"; scope.launch { AppContainer.debridPreferences.setMetadataSource("tmdb") } }
                )
                Text("TMDB (يحتاج مفتاح API)", color = Color.White)
            }
            if (metadataSource == "tmdb") {
                DebridKeyField("TMDB API Key", tmdbKey) {
                    tmdbKey = it
                    scope.launch { AppContainer.debridPreferences.setTmdbKey(it) }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Debrid
        SettingsSection("خدمات Debrid (للمصادر)") {
            Text(
                "أضف مفتاح واحد على الأقل لتفعيل المصادر عالية الجودة",
                color = Color(0xFFB0B0BE), fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp)
            )

            DebridKeyField("Real-Debrid API Key", rdKey) {
                rdKey = it
                scope.launch { AppContainer.debridPreferences.setRealDebridKey(it.trim()) }
            }
            DebridKeyField("TorBox API Key", torboxKey) {
                torboxKey = it
                scope.launch { AppContainer.debridPreferences.setTorboxKey(it.trim()) }
            }
            DebridKeyField("Premiumize API Key", premiumizeKey) {
                premiumizeKey = it
                scope.launch { AppContainer.debridPreferences.setPremiumizeKey(it.trim()) }
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    statusMsg = "جاري تحديث المصادر..."
                    scope.launch {
                        kotlinx.coroutines.delay(500)
                        AppContainer.refreshTorrentio()
                        kotlinx.coroutines.delay(2500)
                        statusMsg = "✅ تم تحديث المصادر"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B5CFF))
            ) {
                Icon(Icons.Default.Refresh, null)
                Spacer(Modifier.width(8.dp))
                Text("تحديث المصادر", fontWeight = FontWeight.Bold)
            }

            statusMsg?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = Color(0xFF00D4B8), fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Addons & Content
        SettingsSection("المحتوى") {
            SettingsButton("إدارة الإضافات", Icons.Default.Extension) { navController.navigate("addons") }
            SettingsButton("المفضلة", Icons.Default.Favorite) { navController.navigate("library") }
        }

        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().background(Color(0xFF16161D), RoundedCornerShape(12.dp)).padding(16.dp)) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF7B5CFF), modifier = Modifier.padding(bottom = 12.dp))
        content()
    }
}

@Composable
private fun DebridKeyField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(label, color = Color(0xFFB0B0BE), fontSize = 13.sp)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("الصق المفتاح هنا", fontSize = 12.sp) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF7B5CFF), unfocusedBorderColor = Color(0xFF2F2F3A)
            )
        )
    }
}

@Composable
private fun SettingsButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F1F2A), contentColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color(0xFF7B5CFF))
            Spacer(Modifier.width(12.dp))
            Text(label, fontSize = 16.sp)
        }
    }
}

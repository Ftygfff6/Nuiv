package com.nuviotv.app.ui.screens.addons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import kotlinx.coroutines.launch

@Composable
fun AddAddonScreen(navController: NavController) {
    var url by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E12))
            .padding(32.dp)
    ) {
        // Back + Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, "رجوع", tint = Color.White)
            }
            Spacer(Modifier.width(8.dp))
            Text("إضافة Addon", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16161D)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    "أدخل رابط الـ Addon",
                    color = Color(0xFF7B5CFF),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "مثال: https://v3-cinemeta.strem.io",
                    color = Color(0xFF7E7E8E),
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("https://...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF7B5CFF),
                        unfocusedBorderColor = Color(0xFF2F2F3A)
                    )
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (url.isBlank()) {
                            status = "❌ أدخل رابط أولاً"
                            return@Button
                        }
                        isLoading = true
                        status = null
                        scope.launch {
                            val result = AppContainer.addonRepository.installAddon(url)
                            isLoading = false
                            if (result.isSuccess) {
                                status = "✅ تم تثبيت: ${result.getOrNull()?.manifest?.name}"
                                url = ""
                            } else {
                                status = "❌ فشل: ${result.exceptionOrNull()?.message}"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B5CFF))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("جاري التثبيت...")
                    } else {
                        Text("تثبيت", fontWeight = FontWeight.Bold)
                    }
                }

                status?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(it, color = Color.White, fontSize = 14.sp)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Suggested addons
        Text(
            "إضافات مقترحة",
            color = Color(0xFF7B5CFF),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val suggestions = listOf(
            "Cinemeta (افتراضي)" to "https://v3-cinemeta.strem.io",
            "Torrentio" to "https://torrentio.strem.fun",
            "OpenSubtitles" to "https://opensubtitles-v3.strem.io",
            "Anime Kitsu" to "https://anime-kitsu.strem.fun",
            "TMDB" to "https://tmdb-addon.strem.io"
        )

        suggestions.forEach { (name, suggestionUrl) ->
            Card(
                onClick = { url = suggestionUrl },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16161D)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(name, color = Color.White, fontSize = 15.sp)
                    Text("إضافة ←", color = Color(0xFF7B5CFF), fontSize = 13.sp)
                }
            }
        }
    }
}

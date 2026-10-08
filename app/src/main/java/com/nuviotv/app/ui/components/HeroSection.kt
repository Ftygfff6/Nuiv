package com.nuviotv.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*

@Composable
fun HeroSection(title: String, subtitle: String, description: String, onPlayClick: () -> Unit, onInfoClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(420.dp).background(Brush.verticalGradient(listOf(Color(0xFF2A1F5C), Color(0xFF0E0E12))))) {
        Column(Modifier.align(Alignment.BottomStart).padding(start = 48.dp, bottom = 40.dp, end = 480.dp)) {
            Text(title, fontSize = 44.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(8.dp))
            Text(subtitle, fontSize = 14.sp, color = Color(0xFFB0B0BE))
            Spacer(Modifier.height(12.dp))
            Text(description, fontSize = 15.sp, color = Color(0xFFD0D0DA), maxLines = 3)
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onPlayClick, shape = ButtonDefaults.shape(shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.colors(containerColor = Color.White, contentColor = Color.Black,
                        focusedContainerColor = Color.White, focusedContentColor = Color.Black)) {
                    Text("▶  تشغيل", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = onInfoClick, shape = ButtonDefaults.shape(shape = RoundedCornerShape(10.dp))) {
                    Text("ℹ  تفاصيل")
                }
            }
        }
    }
}

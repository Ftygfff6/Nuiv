package com.nuviotv.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.tv.material3.*
import com.nuviotv.app.ui.components.HeroSection
import com.nuviotv.app.ui.components.Sidebar
import com.nuviotv.app.ui.theme.NiovBackground
import com.nuviotv.app.ui.theme.NiovSurface

data class MediaCard(val title: String, val subtitle: String)

@Composable
fun HomeScreen(navController: NavController) {
    Row(Modifier.fillMaxSize().background(NiovBackground)) {
        Sidebar(navController)
        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            item {
                HeroSection(
                    title = "مرحبًا بك في Niov",
                    subtitle = "المرحلة 0 — الواجهة الأساسية",
                    description = "تطبيق بث مبني على Stremio Addons، مصمم لـ Google TV.",
                    onPlayClick = { }, onInfoClick = { }
                )
            }
            item { MediaRow("Continue Watching", listOf(
                MediaCard("فيلم 1", "1h 42m"), MediaCard("فيلم 2", "2h 05m"),
                MediaCard("مسلسل 1", "S01E03"), MediaCard("مسلسل 2", "S02E07"),
                MediaCard("وثائقي", "48m"), MediaCard("أنيمي", "E12"))) }
            item { MediaRow("Trending Now", listOf(
                MediaCard("A", "2024"), MediaCard("B", "2023"), MediaCard("C", "2024"),
                MediaCard("D", "2022"), MediaCard("E", "2024"), MediaCard("F", "2021"))) }
            item { MediaRow("Your Addons", listOf(
                MediaCard("Cinemeta", "Official"), MediaCard("Torrentio", "Debrid"),
                MediaCard("OpenSubtitles", "Subs"), MediaCard("Trakt", "Sync"),
                MediaCard("YouTube", "Trailers"), MediaCard("Kitsu", "Anime"))) }
            item { Spacer(Modifier.height(48.dp)) }
        }
    }
}

@Composable
private fun MediaRow(title: String, items: List<MediaCard>) {
    Column {
        Text(title, Modifier.padding(start = 48.dp, bottom = 12.dp), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        LazyRow(contentPadding = PaddingValues(horizontal = 48.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(items) { MediaCardView(it) }
        }
    }
}

@Composable
private fun MediaCardView(item: MediaCard) {
    Card(
        onClick = { }, modifier = Modifier.width(200.dp).height(280.dp),
        shape = ClickableSurfaceDefaults.shape(shape = RoundedCornerShape(12.dp)),
        colors = ClickableSurfaceDefaults.colors(containerColor = NiovSurface, focusedContainerColor = NiovSurface),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.08f)
    ) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.Start) {
            Text(item.title, Modifier.padding(12.dp), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            Text(item.subtitle, Modifier.padding(start = 12.dp, bottom = 12.dp), fontSize = 12.sp, color = Color(0xFFB0B0BE))
        }
    }
}

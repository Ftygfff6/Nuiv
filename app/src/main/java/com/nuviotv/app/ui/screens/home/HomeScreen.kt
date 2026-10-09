package com.nuviotv.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nuviotv.app.data.model.Meta
import com.nuviotv.app.data.repository.CatalogResult

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    Column(Modifier.fillMaxSize().background(Color(0xFF0E0E12))) {
        if (state.hasUpdate) {
            UpdateBanner(state.updateVersion, state.isDownloading,
                onUpdate = { viewModel.startUpdate() },
                onDismiss = { viewModel.dismissUpdate() })
        }
        when {
            state.isLoading -> LoadingScreen()
            state.error != null -> ErrorScreen(state.error!!) { viewModel.loadContent() }
            state.movies.isEmpty() && state.series.isEmpty() -> EmptyScreen(navController)
            else -> ContentList(state.movies, state.series)
        }
    }
}

@Composable
private fun UpdateBanner(version: String?, isDownloading: Boolean, onUpdate: () -> Unit, onDismiss: () -> Unit) {
    Box(Modifier.fillMaxWidth()
        .background(Brush.horizontalGradient(listOf(Color(0xFF7B5CFF), Color(0xFF5B3FE0))))
        .padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Update, null, tint = Color.White, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(if (isDownloading) "جاري تحميل التحديث..." else "🎉 يوجد تحديث جديد!",
                    color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(version?.let { "الإصدار: $it" } ?: "اضغط تحديث لتثبيت آخر إصدار",
                    color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
            }
            if (isDownloading) {
                CircularProgressIndicator(Modifier.size(28.dp), color = Color.White, strokeWidth = 3.dp)
            } else {
                Button(onClick = onUpdate,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White, contentColor = Color(0xFF7B5CFF)),
                    shape = RoundedCornerShape(8.dp)) {
                    Text("تحديث الآن", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, "إغلاق", tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color(0xFF7B5CFF))
            Spacer(Modifier.height(16.dp))
            Text("جاري تحميل المحتوى...", color = Color(0xFFB0B0BE))
        }
    }
}

@Composable
private fun ErrorScreen(error: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("خطأ: $error", color = Color(0xFFFF4D6D))
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry) { Text("إعادة المحاولة") }
        }
    }
}

@Composable
private fun EmptyScreen(navController: NavController) {
    Column(Modifier.fillMaxSize().padding(48.dp)) {
        Text("مرحبًا بك في Niov", fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(Modifier.height(16.dp))
        Text("جاري تثبيت Cinemeta تلقائيًا...", fontSize = 16.sp, color = Color(0xFFB0B0BE))
        Spacer(Modifier.height(24.dp))
        Button(onClick = { navController.navigate("addons") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B5CFF))) {
            Icon(Icons.Default.Extension, null)
            Spacer(Modifier.width(8.dp))
            Text("إدارة الإضافات")
        }
    }
}

@Composable
private fun ContentList(movies: List<CatalogResult>, series: List<CatalogResult>) {
    LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item { movies.firstOrNull()?.metas?.firstOrNull()?.let { HeroSection(it) } }
        items(movies) { c -> CatalogRow("${c.catalogName} — ${c.addonName}", c.metas) }
        items(series) { c -> CatalogRow("${c.catalogName} — ${c.addonName}", c.metas) }
        item { Spacer(Modifier.height(48.dp)) }
    }
}

@Composable
private fun HeroSection(meta: Meta) {
    Box(Modifier.fillMaxWidth().height(420.dp)) {
        if (!meta.background.isNullOrBlank()) {
            AsyncImage(meta.background, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else Box(Modifier.fillMaxSize().background(Color(0xFF2A1F5C)))
        Box(Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC0E0E12), Color(0xFF0E0E12)))))
        Column(Modifier.align(Alignment.BottomStart).padding(start = 48.dp, bottom = 40.dp, end = 480.dp)) {
            Text(meta.name, fontSize = 44.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(8.dp))
            Row {
                meta.releaseInfo?.let { Text(it, color = Color(0xFFB0B0BE), fontSize = 14.sp); Spacer(Modifier.width(12.dp)) }
                meta.imdbRating?.let { Text("⭐ $it", color = Color(0xFFFFC107), fontSize = 14.sp) }
            }
            Spacer(Modifier.height(12.dp))
            meta.description?.let { Text(it, fontSize = 15.sp, color = Color(0xFFD0D0DA), maxLines = 3) }
        }
    }
}

@Composable
private fun CatalogRow(title: String, metas: List<Meta>) {
    if (metas.isEmpty()) return
    Column {
        Text(title, Modifier.padding(start = 48.dp, bottom = 12.dp),
            fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        LazyRow(contentPadding = PaddingValues(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(metas) { PosterCard(it) }
        }
    }
}

@Composable
private fun PosterCard(meta: Meta) {
    Card(onClick = { }, modifier = Modifier.width(180.dp).height(270.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16161D)),
        shape = RoundedCornerShape(12.dp)) {
        Box(Modifier.fillMaxSize()) {
            if (!meta.poster.isNullOrBlank()) {
                AsyncImage(meta.poster, meta.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else Box(Modifier.fillMaxSize().background(Color(0xFF1F1F2A)))
            Box(Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC000000))))
                .padding(8.dp)) {
                Text(meta.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                    color = Color.White, maxLines = 2)
            }
        }
    }
}

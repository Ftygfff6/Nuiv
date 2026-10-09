package com.nuviotv.app.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@Composable
fun DetailScreen(
    navController: NavController,
    type: String,
    id: String
) {
    val viewModel: DetailViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(type, id) { viewModel.load(type, id) }

    Box(Modifier.fillMaxSize().background(Color(0xFF0E0E12))) {
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF7B5CFF))
            }
            state.meta == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لم يتم العثور على البيانات", color = Color.White)
            }
            else -> {
                LazyColumn(Modifier.fillMaxSize()) {
                    // Hero
                    item {
                        Box(Modifier.fillMaxWidth().height(420.dp)) {
                            if (!state.meta!!.background.isNullOrBlank()) {
                                AsyncImage(
                                    model = state.meta!!.background,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(Modifier.fillMaxSize().background(Color(0xFF2A1F5C)))
                            }
                            Box(Modifier.fillMaxSize().background(
                                Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC0E0E12), Color(0xFF0E0E12)))
                            ))
                            Column(Modifier.align(Alignment.BottomStart).padding(48.dp)) {
                                Text(state.meta!!.name, fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(Modifier.height(8.dp))
                                Row {
                                    state.meta!!.releaseInfo?.let {
                                        Text(it, color = Color(0xFFB0B0BE), fontSize = 14.sp); Spacer(Modifier.width(12.dp))
                                    }
                                    state.meta!!.imdbRating?.let {
                                        Text("⭐ $it", color = Color(0xFFFFC107), fontSize = 14.sp)
                                    }
                                }
                                Spacer(Modifier.height(16.dp))
                                state.meta!!.description?.let {
                                    Text(it, fontSize = 14.sp, color = Color(0xFFD0D0DA), maxLines = 4)
                                }
                            }
                        }
                    }

                    // Sources
                    item {
                        Text(
                            "المصادر (${state.streams.size})",
                            fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White,
                            modifier = Modifier.padding(start = 48.dp, top = 24.dp, bottom = 12.dp)
                        )
                    }

                    if (state.streams.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.CloudOff, null, tint = Color(0xFF7E7E8E), modifier = Modifier.size(48.dp))
                                    Spacer(Modifier.height(12.dp))
                                    Text("لا توجد مصادر", color = Color(0xFF7E7E8E))
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "أضف مفتاح Debrid من الإعدادات لتفعيل المصادر",
                                        color = Color(0xFF5E5E6E), fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(state.streams) { streamResult ->
                            StreamCard(streamResult, onClick = {
                                // TODO: play stream
                            })
                        }
                    }

                    item { Spacer(Modifier.height(48.dp)) }
                }
            }
        }
    }
}

@Composable
private fun StreamCard(stream: com.nuviotv.app.data.repository.StreamResult, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16161D)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                stream.stream.title ?: stream.stream.name ?: "مصدر",
                color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                stream.stream.name ?: stream.addonName,
                color = Color(0xFF7B5CFF), fontSize = 12.sp
            )
            if (stream.stream.url != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "🔗 Direct",
                    color = Color(0xFF00D4B8), fontSize = 11.sp
                )
            } else if (stream.stream.infoHash != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "🧲 Magnet · ${stream.stream.infoHash.take(12)}...",
                    color = Color(0xFFB0B0BE), fontSize = 11.sp
                )
            }
        }
    }
}

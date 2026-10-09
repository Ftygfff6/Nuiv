package com.nuviotv.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    Column(Modifier.fillMaxSize().background(Color(0xFF0E0E12))) {
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF7B5CFF))
                    Spacer(Modifier.height(16.dp))
                    Text("جاري تحميل المحتوى...", color = Color(0xFFB0B0BE))
                }
            }
        } else {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(32.dp)
            ) {
                Text("تشخيص Niov", fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(24.dp))

                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF16161D)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("حالة النظام", color = Color(0xFF7B5CFF), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            state.debugInfo ?: "لا توجد تفاصيل",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF16161D)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("النتائج", color = Color(0xFF7B5CFF), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("أفلام: ${state.movies.size} كتالوج، ${state.movies.sumOf { it.metas.size }} فيلم",
                            color = Color.White, fontSize = 14.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("مسلسلات: ${state.series.size} كتالوج، ${state.series.sumOf { it.metas.size }} مسلسل",
                            color = Color.White, fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.loadContent() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B5CFF))
                ) {
                    Icon(Icons.Default.Refresh, null)
                    Spacer(Modifier.width(8.dp))
                    Text("إعادة المحاولة", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { navController.navigate("addons") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F1F2A))
                ) {
                    Text("إدارة الإضافات")
                }
            }
        }
    }
}

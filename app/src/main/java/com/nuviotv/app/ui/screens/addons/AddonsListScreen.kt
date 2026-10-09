package com.nuviotv.app.ui.screens.addons

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nuviotv.app.data.model.AddonConfig
import com.nuviotv.app.di.AppContainer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun AddonsListScreen(navController: NavController) {
    var addons by remember { mutableStateOf<List<AddonConfig>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        AppContainer.addonRepository.installedAddons.collectLatest {
            addons = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E12))
            .padding(32.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, "رجوع", tint = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                Text("الإضافات المثبتة", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Button(
                onClick = { navController.navigate("add_addon") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B5CFF)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(4.dp))
                Text("إضافة جديد")
            }
        }

        Spacer(Modifier.height(24.dp))

        if (addons.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Extension, null, tint = Color(0xFF7E7E8E), modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("لا توجد إضافات", color = Color(0xFF7E7E8E), fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("اضغط \"إضافة جديد\" للبدء", color = Color(0xFF5E5E6E), fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(addons, key = { it.transportUrl }) { addon ->
                    AddonCard(
                        addon = addon,
                        onToggle = { enabled ->
                            scope.launch {
                                AppContainer.addonRepository.toggleAddon(addon.transportUrl, enabled)
                            }
                        },
                        onDelete = {
                            scope.launch {
                                AppContainer.addonRepository.removeAddon(addon.transportUrl)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AddonCard(
    addon: AddonConfig,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16161D)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        addon.manifest.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "v${addon.manifest.version}",
                        color = Color(0xFF7E7E8E),
                        fontSize = 12.sp
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    addon.manifest.description ?: "لا يوجد وصف",
                    color = Color(0xFFB0B0BE),
                    fontSize = 13.sp,
                    maxLines = 2
                )
                Spacer(Modifier.height(8.dp))
                Row {
                    addon.manifest.types.take(4).forEach { type ->
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF2F2F3A), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .padding(end = 4.dp)
                        ) {
                            Text(type, color = Color(0xFFB0B0BE), fontSize = 11.sp)
                        }
                        Spacer(Modifier.width(4.dp))
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Switch(
                    checked = addon.isEnabled,
                    onCheckedChange = onToggle
                )
                Spacer(Modifier.height(4.dp))
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "حذف", tint = Color(0xFFFF4D6D))
                }
            }
        }
    }
}

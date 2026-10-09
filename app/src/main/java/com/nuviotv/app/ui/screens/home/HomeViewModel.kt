package com.nuviotv.app.ui.screens.home

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nuviotv.app.data.repository.CatalogResult
import com.nuviotv.app.data.update.UpdateManager
import com.nuviotv.app.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val movies: List<CatalogResult> = emptyList(),
    val series: List<CatalogResult> = emptyList(),
    val error: String? = null,
    val hasUpdate: Boolean = false,
    val updateVersion: String? = null,
    val apkUrl: String? = null,
    val isDownloading: Boolean = false
)

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()
    private var downloadId: Long = -1

    init {
        loadContent()
        checkUpdate()
    }

    fun loadContent() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val movies = AppContainer.addonRepository.loadCatalogs("movie")
                val series = AppContainer.addonRepository.loadCatalogs("series")
                _state.value = _state.value.copy(isLoading = false, movies = movies, series = series)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message ?: "خطأ")
            }
        }
    }

    private fun checkUpdate() {
        viewModelScope.launch {
            val info = UpdateManager.checkForUpdate(1)
            _state.value = _state.value.copy(
                hasUpdate = info.hasUpdate,
                updateVersion = info.version,
                apkUrl = info.apkUrl
            )
        }
    }

    fun startUpdate() {
        val ctx = getApplication<Application>()
        val apkUrl = _state.value.apkUrl ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            !ctx.packageManager.canRequestPackageInstalls()) {
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = Uri.parse("package:${ctx.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            ctx.startActivity(intent)
            return
        }

        _state.value = _state.value.copy(isDownloading = true)
        downloadId = UpdateManager.startDownload(ctx, apkUrl)
        UpdateManager.registerDownloadReceiver(ctx, downloadId) {
            viewModelScope.launch {
                _state.value = _state.value.copy(isDownloading = false)
                kotlinx.coroutines.delay(1000)
                UpdateManager.installApk(ctx)
            }
        }
    }

    fun dismissUpdate() { _state.value = _state.value.copy(hasUpdate = false) }
}

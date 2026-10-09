package com.nuviotv.app.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuviotv.app.data.model.Meta
import com.nuviotv.app.data.model.Stream
import com.nuviotv.app.data.repository.StreamResult
import com.nuviotv.app.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetailUiState(
    val isLoading: Boolean = true,
    val meta: Meta? = null,
    val streams: List<StreamResult> = emptyList(),
    val error: String? = null
)

class DetailViewModel : ViewModel() {
    private val _state = MutableStateFlow(DetailUiState())
    val state: StateFlow<DetailUiState> = _state.asStateFlow()

    fun load(type: String, id: String) {
        viewModelScope.launch {
            _state.value = DetailUiState(isLoading = true)
            try {
                val meta = AppContainer.addonRepository.loadMeta(type, id)
                val streams = AppContainer.addonRepository.loadStreams(type, id)
                _state.value = DetailUiState(isLoading = false, meta = meta, streams = streams)
            } catch (e: Exception) {
                _state.value = DetailUiState(isLoading = false, error = e.message)
            }
        }
    }
}

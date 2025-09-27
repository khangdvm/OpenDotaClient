package com.example.opendotaclient.ui.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opendotaclient.data.TeamUI
import com.example.opendotaclient.data.remote.TeamsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class TeamsState(
    val loading: Boolean = false,
    val items: List<TeamUI> = emptyList(),
    val error: String? = null
)

class TeamsViewModel(
    private val repo: TeamsRepository,
    private val apiKey: String? = null
) : ViewModel() {

    private val _state = MutableStateFlow(TeamsState(loading = true))
    val state: StateFlow<TeamsState> = _state

    fun load(limit: Int = 50) {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val data = repo.getTopTeams(limit, apiKey)
                _state.value = TeamsState(loading = false, items = data, error = null)
            } catch (e: Exception) {
                _state.value = TeamsState(loading = false, items = emptyList(), error = e.message)
            }
        }
    }
}

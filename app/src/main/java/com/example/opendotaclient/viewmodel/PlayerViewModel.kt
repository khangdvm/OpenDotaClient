package com.example.opendotaclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opendotaclient.data.model.PlayerProfile
import com.example.opendotaclient.data.model.RecentMatch
import com.example.opendotaclient.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlayerState(
    val loading: Boolean = false,
    val profile: PlayerProfile? = null,
    val matches: List<RecentMatch> = emptyList(),
    val error: String? = null
)

class PlayerViewModel : ViewModel() {
    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state

    fun load(accountId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching {
                val p = ApiClient.api.getPlayer(accountId)
                val m = ApiClient.api.getRecentMatches(accountId)
                p to m
            }.onSuccess { (p, m) ->
                _state.update { it.copy(loading = false, profile = p, matches = m) }
            }.onFailure { e ->
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }
}

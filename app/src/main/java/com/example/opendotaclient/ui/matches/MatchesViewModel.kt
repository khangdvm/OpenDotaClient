

package com.example.opendotaclient.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opendotaclient.data.remote.MatchesRepository
import com.example.opendotaclient.data.remote.RecentMatchDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RecentMatchUI(
    val matchId: Long,
    val startTime: Long?,
    val duration: Int?,
    val heroId: Int?,
    val k: Int?, val d: Int?, val a: Int?,
    val didWin: Boolean?
)

class MatchesViewModel(
    private val repo: MatchesRepository,
    private val accountId32: Long,
    private val apiKey: String? = null
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _items = MutableStateFlow<List<RecentMatchUI>>(emptyList())
    val items: StateFlow<List<RecentMatchUI>> = _items

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init { load() }

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val raw: List<RecentMatchDTO> = repo.getRecentMatches(accountId32, apiKey)
                _items.value = raw.map { dto -> dto.toUI() }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load matches"
            } finally {
                _loading.value = false
            }
        }
    }
}

// ---- mapping lives in UI layer ----
private fun RecentMatchDTO.toUI(): RecentMatchUI {
    val slot = playerSlot ?: 0
    val isRadiant = (slot and 0x80) == 0
    val didWin = radiantWin?.let { if (isRadiant) it else !it }
    return RecentMatchUI(
        matchId = matchId,
        startTime = startTime,
        duration = duration,
        heroId = heroId,
        k = kills, d = deaths, a = assists,
        didWin = didWin
    )
}

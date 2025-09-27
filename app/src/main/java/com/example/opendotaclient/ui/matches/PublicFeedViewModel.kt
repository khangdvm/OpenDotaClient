package com.example.opendotaclient.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opendotaclient.data.remote.PublicMatchDTO
import com.example.opendotaclient.data.remote.PublicMatchesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PublicFeedItemUI(
    val matchId: Long,
    val startTime: Long?,
    val duration: Int?,
    val avgMmr: Int?,
    val radiantWin: Boolean?
)

class PublicFeedViewModel(private val repo: PublicMatchesRepository) : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _items = MutableStateFlow<List<PublicFeedItemUI>>(emptyList())
    val items: StateFlow<List<PublicFeedItemUI>> = _items

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init { load() }

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val list = repo.getPublicMatches(mmrDesc = null, lessThanMatchId = null)
                _items.value = list.map { it.toUI() }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    private fun PublicMatchDTO.toUI() = PublicFeedItemUI(
        matchId = matchId,
        startTime = startTime,
        duration = duration,
        avgMmr = avgMmr,
        radiantWin = radiantWin
    )
}

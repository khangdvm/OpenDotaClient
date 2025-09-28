// app/src/main/java/com/example/opendotaclient/ui/matches/PublicFeedViewModel.kt
package com.example.opendotaclient.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opendotaclient.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MatchLite(
    val id: Long,
    val avgMmr: Int?,
    val durationSec: Int?,
    val radiantWin: Boolean?
)

class PublicFeedViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<MatchLite>>(emptyList())
    val items: StateFlow<List<MatchLite>> = _items

    val loading = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)

    fun load() {
        viewModelScope.launch {
            loading.value = true
            error.value = null
            try {
                val api = RetrofitClient.api
                val raw = api.getPublicMatches()
                _items.value = raw.map { dto ->
                    MatchLite(
                        id = dto.matchId,
                        avgMmr = dto.avgMmr,
                        durationSec = dto.duration,
                        radiantWin = dto.radiantWin
                    )
                }
            } catch (e: Exception) {
                error.value = e.message
            } finally {
                loading.value = false
            }
        }
    }
}

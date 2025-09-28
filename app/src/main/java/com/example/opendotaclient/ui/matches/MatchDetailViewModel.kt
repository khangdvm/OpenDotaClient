package com.example.opendotaclient.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opendotaclient.data.remote.HeroesRepository
import com.example.opendotaclient.data.remote.MatchDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MatchDetailState(
    val loading: Boolean = false,
    val heroImageUrls: List<String> = emptyList(),
    val error: String? = null
)

class MatchDetailViewModel(
    private val matchId: Long,
    private val detailRepo: MatchDetailRepository,
    private val heroesRepo: HeroesRepository,
    private val apiKey: String? = null
) : ViewModel() {

    private val _state = MutableStateFlow(MatchDetailState(loading = true))
    val state: StateFlow<MatchDetailState> = _state

    fun load() {
        _state.value = MatchDetailState(loading = true)
        viewModelScope.launch {
            try {
                // 1) Lấy danh sách hero_id xuất hiện trong trận
                val ids: List<Int> = detailRepo.getHeroIdsInMatch(matchId, apiKey)

                // 2) Lấy catalog heroes (HeroUI) — đã có imageUrl FULL
                val catalog = heroesRepo.getHeroes(apiKey)            // List<HeroUI>
                val imgMap: Map<Int, String?> = catalog.associate { it.id to it.imageUrl }

                // 3) Map hero_id -> imageUrl (bỏ null)
                val urls: List<String> = ids.mapNotNull { hid -> imgMap[hid] }

                _state.value = MatchDetailState(
                    loading = false,
                    heroImageUrls = urls,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = MatchDetailState(
                    loading = false,
                    heroImageUrls = emptyList(),
                    error = e.message
                )
            }
        }
    }
}

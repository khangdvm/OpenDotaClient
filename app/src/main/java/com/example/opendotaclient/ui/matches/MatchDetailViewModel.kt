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
                // 1. Lấy hero_id trong trận
                val ids = detailRepo.getHeroIdsInMatch(matchId, apiKey)

                // 2. Lấy hero stats (có field img)
                val stats = heroesRepo.getHeroes(apiKey)
                val imgMap = stats.associateBy({ it.id }, { it.img })

                // 3. Map hero_id -> url đầy đủ
                val cdn = "https://cdn.cloudflare.steamstatic.com"
                val urls = ids.mapNotNull { hid ->
                    imgMap[hid]?.let { cdn + it }
                }

                _state.value = MatchDetailState(loading = false, heroImageUrls = urls)

            } catch (e: Exception) {
                _state.value = MatchDetailState(loading = false, error = e.message)
            }
        }
    }
}

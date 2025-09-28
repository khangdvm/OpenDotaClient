package com.example.opendotaclient.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opendotaclient.data.remote.HeroesRepository
import com.example.opendotaclient.data.remote.MatchesRepository
import com.example.opendotaclient.data.remote.RecentMatchDTO
import com.example.opendotaclient.model.PlayerHeroUI
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class PlayerSection { RECENT_MATCHES, HEROES_PLAYED }

class MatchesViewModel(
    private val repo: MatchesRepository,
    private val heroesRepo: HeroesRepository,
    private val accountId32: Long,
    private val apiKey: String?
) : ViewModel() {

    private val _section = MutableStateFlow(PlayerSection.RECENT_MATCHES)
    val section: StateFlow<PlayerSection> = _section

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Recent matches hiển thị bằng DTO gốc cho nhanh
    val recent = MutableStateFlow<List<RecentMatchDTO>>(emptyList())

    // Heroes Played dùng UI model
    val heroesPlayed = MutableStateFlow<List<PlayerHeroUI>>(emptyList())

    fun setSection(s: PlayerSection) { _section.value = s }

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                // lấy catalog hero (để map id -> name, image)
                val heroCatalogDeferred = async { heroesRepo.getHeroes(apiKey).associateBy { it.id } }
                val recentDeferred = async { repo.getRecentMatches(accountId32, apiKey) }
                val heroStatsDeferred = async { repo.getPlayerHeroes(accountId32) }

                val heroCatalog = heroCatalogDeferred.await()
                recent.value = recentDeferred.await()

                val heroStats = heroStatsDeferred.await()
                heroesPlayed.value = heroStats.map { stat ->
                    val h = heroCatalog[stat.heroId]
                    PlayerHeroUI(
                        heroId = stat.heroId,
                        name = h?.name ?: "Hero ${stat.heroId}",
                        imageUrl = h?.imageUrl,
                        games = stat.games,
                        win = stat.win
                    )
                }.sortedByDescending { it.games }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}

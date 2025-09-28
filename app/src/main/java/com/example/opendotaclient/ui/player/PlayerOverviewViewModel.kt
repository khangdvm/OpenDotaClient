package com.example.opendotaclient.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opendotaclient.data.PlayerOverviewRepository
import com.example.opendotaclient.data.PlayerOverviewUI
import com.example.opendotaclient.data.remote.HeroStatDTO
import com.example.opendotaclient.data.remote.OpenDotaService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class PlayerSection { OVERVIEW, RECENT_MATCHES, HEROES_PLAYED }

/* Rows cho 2 tab */
data class RecentMatchRowUI(
    val matchId: Long,
    val heroId: Int?,
    val heroName: String,
    val heroImg: String?,
    val k: Int, val d: Int, val a: Int,
    val durationSec: Int,
    val result: String
)

data class HeroPlayedRowUI(
    val heroId: Int,
    val heroName: String,
    val heroImg: String?,
    val games: Int,
    val wins: Int,
    val winRate: Int
)

/* Win-count theo phía (Dire/Radiant) để vẽ donut */
data class SideWinUI(
    val label: String,
    val wins: Int,
    val losses: Int,
    val winRate: Int
)

data class PlayerState(
    val loading: Boolean = false,
    val error: String? = null,
    val section: PlayerSection = PlayerSection.OVERVIEW,
    val overview: PlayerOverviewUI? = null,
    val rankIcon: String? = null,
    val recentMatches: List<RecentMatchRowUI> = emptyList(),
    val heroesPlayed: List<HeroPlayedRowUI> = emptyList(),
    val dire: SideWinUI? = null,
    val radiant: SideWinUI? = null
)

class PlayerOverviewViewModel(
    private val id32: Long,
    private val overviewRepo: PlayerOverviewRepository,
    private val api: OpenDotaService
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerState(loading = true))
    val state: StateFlow<PlayerState> = _state

    private fun toCdn(path: String?): String? =
        when {
            path.isNullOrBlank() -> null
            path.startsWith("http") -> path
            else -> "https://cdn.cloudflare.steamstatic.com$path"
        }

    /** Map rank_tier -> URL icon (1..8: Herald..Immortal) */
    private fun rankIconUrl(rankTier: Int?): String? {
        val tier = rankTier ?: return null
        val medal = (tier / 10).coerceIn(1, 8) // 8 = Immortal
        return "https://cdn.cloudflare.steamstatic.com/apps/dota2/images/dota_react/rank_icons/rank_icon_$medal.png"
    }

    fun load() {
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                // 1) Overview
                val ov = overviewRepo.load(id32)

                // 2) Hero stats để map ảnh + tên
                val heroStats: List<HeroStatDTO> = runCatching { api.getHeroStats() }.getOrDefault(emptyList())
                val heroById = heroStats.associateBy { it.id }
                fun heroName(id: Int?): String = id?.let { heroById[it]?.localizedName } ?: "Unknown"
                fun heroImg(id: Int?): String? = id?.let { toCdn(heroById[it]?.img) }

                // 3) Recent matches + tính Dire/Radiant wr
                val rec = runCatching { api.getRecentMatches(id32) }.getOrNull().orEmpty()
                var direW = 0; var direL = 0; var radW = 0; var radL = 0

                val recentRows = rec.map { m ->
                    val isRadiant = (m.playerSlot ?: 0) and 0x80 == 0
                    val win = m.radiantWin == true
                    val isWin = (isRadiant && win) || (!isRadiant && !win)

                    if (isRadiant) {
                        if (isWin) radW++ else radL++
                    } else {
                        if (isWin) direW++ else direL++
                    }

                    RecentMatchRowUI(
                        matchId = m.matchId,
                        heroId = m.heroId,
                        heroName = heroName(m.heroId),
                        heroImg = heroImg(m.heroId),
                        k = m.kills ?: 0, d = m.deaths ?: 0, a = m.assists ?: 0,
                        durationSec = m.duration ?: 0,
                        result = if (isWin) "WIN" else "LOSS"
                    )
                }

                val direWR = if (direW + direL == 0) 0 else (direW * 100.0 / (direW + direL)).roundToInt()
                val radWR  = if (radW + radL == 0) 0 else (radW  * 100.0 / (radW  + radL )).roundToInt()

                // 4) Heroes played
                val hp = runCatching { api.getPlayerHeroes(id32) }.getOrNull().orEmpty()
                val heroesRows = hp.map { h ->
                    val wr = if (h.games == 0) 0 else ((h.win * 100.0 / h.games).roundToInt())
                    HeroPlayedRowUI(
                        heroId = h.heroId,
                        heroName = heroName(h.heroId),
                        heroImg = heroImg(h.heroId),
                        games = h.games,
                        wins = h.win,
                        winRate = wr
                    )
                }

                _state.update {
                    it.copy(
                        loading = false,
                        overview = ov,
                        rankIcon = rankIconUrl(ov.rankTier),
                        recentMatches = recentRows,
                        heroesPlayed = heroesRows,
                        dire = SideWinUI("Dire", direW, direL, direWR),
                        radiant = SideWinUI("Radiant", radW, radL, radWR)
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message ?: "Load error") }
            }
        }
    }

    fun setSection(s: PlayerSection) = _state.update { it.copy(section = s) }
}

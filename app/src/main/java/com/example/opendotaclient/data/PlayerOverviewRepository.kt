package com.example.opendotaclient.data

import com.example.opendotaclient.data.remote.OpenDotaService

class PlayerOverviewRepository(private val api: OpenDotaService) {

    suspend fun load(id32: Long): PlayerOverviewUI {
        val p = api.getPlayer(id32)
        val wl = api.getPlayerWl(id32)

        val wins = wl.win
        val losses = wl.lose
        val total = (wins + losses).coerceAtLeast(1)
        val wr = wins * 100.0 / total

        val recent = runCatching { api.getRecentMatches(id32) }.getOrNull()
        val avgGpm = recent?.mapNotNull { it.gpm }?.average()?.toInt()
        val avgXpm = recent?.mapNotNull { it.xpm }?.average()?.toInt()
        val avgLH  = recent?.mapNotNull { it.lastHits }?.average()?.toInt()
        val avgHD  = recent?.mapNotNull { it.heroDamage }?.average()?.toInt()

        val name = p.profile?.personaname ?: p.profile?.name ?: "Player $id32"
        return PlayerOverviewUI(
            name = name,
            avatar = p.profile?.avatarfull,
            wins = wins,
            losses = losses,
            winrate = wr,
            rankTier = p.rankTier,
            leaderboardRank = p.leaderboardRank,
            avgGpm = avgGpm,
            avgXpm = avgXpm,
            avgLastHits = avgLH,
            avgHeroDmg = avgHD
        )
    }
}

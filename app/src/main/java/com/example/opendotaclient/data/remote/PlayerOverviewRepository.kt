package com.example.opendotaclient.data

import com.example.opendotaclient.data.remote.OpenDotaService

class PlayerOverviewRepository(private val api: OpenDotaService) {
    suspend fun load(id32: Long): PlayerOverviewUI {
        val player = api.getPlayer(id32)
        val heroes = api.getPlayerHeroes(id32)
        val ranks = api.getPlayerRankings(id32)

        return PlayerOverviewUI(
            name = player.profile?.personaname
                ?: player.profile?.name
                ?: "Player $id32",
            avatar = player.profile?.avatarfull,
            topHeroes = heroes.sortedByDescending { it.games }.take(5),
            rankings = ranks
        )
    }
}

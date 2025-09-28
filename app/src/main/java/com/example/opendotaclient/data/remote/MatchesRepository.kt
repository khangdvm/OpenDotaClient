package com.example.opendotaclient.data.remote

import com.example.opendotaclient.data.remote.OpenDotaService
import com.example.opendotaclient.data.remote.RecentMatchDTO
import com.example.opendotaclient.data.remote.PublicMatchDTO
import com.example.opendotaclient.data.remote.PlayerDTO
import com.example.opendotaclient.data.remote.PlayerRankingDTO
import com.example.opendotaclient.data.remote.PlayerHeroStatDTO

class MatchesRepository(
    private val api: OpenDotaService
) {
    suspend fun getRecentMatches(
        accountId32: Long,
        apiKey: String? = null
    ): List<RecentMatchDTO> = api.getRecentMatches(accountId32, apiKey)

    suspend fun getPublicMatches(
        mmrDesc: Int? = null,
        lessThanMatchId: Long? = null
    ): List<PublicMatchDTO> = api.getPublicMatches(mmrDesc, lessThanMatchId)

    suspend fun getPlayer(accountId32: Long): PlayerDTO =
        api.getPlayer(accountId32)

    suspend fun getPlayerHeroes(accountId32: Long): List<PlayerHeroStatDTO> =
        api.getPlayerHeroes(accountId32)

    suspend fun getPlayerRankings(accountId32: Long): List<PlayerRankingDTO> =
        api.getPlayerRankings(accountId32)
}

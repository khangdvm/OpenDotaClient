package com.example.opendotaclient.data.remote

class MatchesRepository(
    private val api: OpenDotaService
) {
    // TRẢ VỀ DTO, KHÔNG map sang UI
    suspend fun getRecentMatches(
        accountId32: Long,
        apiKey: String? = null
    ): List<RecentMatchDTO> = api.getRecentMatches(accountId32, apiKey)

    suspend fun getPublicMatches(
        mmrDesc: Int? = null,
        lessThanMatchId: Long? = null
    ): List<PublicMatchDTO> = api.getPublicMatches(mmrDesc, lessThanMatchId)

    suspend fun getPlayer(accountId32: Long): PlayerDTO = api.getPlayer(accountId32)
    suspend fun getPlayerHeroes(accountId32: Long): List<PlayerHeroDTO> = api.getPlayerHeroes(accountId32)
    suspend fun getPlayerRankings(accountId32: Long): List<PlayerRankingDTO> = api.getPlayerRankings(accountId32)
}

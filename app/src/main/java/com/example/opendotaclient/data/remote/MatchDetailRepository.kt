package com.example.opendotaclient.data.remote

class MatchDetailRepository(
    private val api: OpenDotaService
) {
    suspend fun getHeroIdsInMatch(matchId: Long, apiKey: String? = null): List<Int> {
        val md = api.getMatch(matchId, apiKey)
        return md.players.mapNotNull { it.heroId }
    }
}

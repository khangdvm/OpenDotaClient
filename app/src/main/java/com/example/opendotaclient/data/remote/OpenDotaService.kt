package com.example.opendotaclient.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenDotaService {

    // Recent matches theo Steam32 account_id
    @GET("players/{account_id}/recentMatches")
    suspend fun getRecentMatches(
        @Path("account_id") accountId: Long,
        @Query("api_key") apiKey: String? = null
    ): List<RecentMatchDTO>

    // Dùng để map hero_id -> tên hero
    @GET("heroes")
    suspend fun getHeroes(
        @Query("api_key") apiKey: String? = null
    ): List<HeroDTO>

    // Hero stats (ảnh, roles, primary attr, attack type…)
    @GET("heroStats")
    suspend fun getHeroStats(
        @Query("api_key") apiKey: String? = null
    ): List<HeroStatDTO>

    // Teams (pro teams info)
    @GET("teams")
    suspend fun getTeams(
        @Query("api_key") apiKey: String? = null
    ): List<TeamDTO>

    // Recent public matches feed (like /matches)
    @GET("publicMatches")
    suspend fun getPublicMatches(
        @Query("mmr_descending") mmrDesc: Int? = null,      // optional
        @Query("less_than_match_id") lessThanMatchId: Long? = null,
        @Query("api_key") apiKey: String? = null           // cho đồng bộ
    ): List<PublicMatchDTO>

    // Player profile summary
    @GET("players/{account_id}")
    suspend fun getPlayer(
        @Path("account_id") id: Long,
        @Query("api_key") apiKey: String? = null
    ): PlayerDTO

    // Player heroes (most played)
    @GET("players/{account_id}/heroes")
    suspend fun getPlayerHeroes(
        @Path("account_id") id: Long,
        @Query("api_key") apiKey: String? = null
    ): List<PlayerHeroDTO>

    // Player hero rankings (per hero)
    @GET("players/{account_id}/rankings")
    suspend fun getPlayerRankings(
        @Path("account_id") id: Long,
        @Query("api_key") apiKey: String? = null
    ): List<PlayerRankingDTO>

    // com/example/opendotaclient/data/remote/OpenDotaService.kt
    @GET("matches/{match_id}")
    suspend fun getMatch(
        @Path("match_id") matchId: Long,
        @Query("api_key") apiKey: String? = null
    ): MatchDetailDTO

}

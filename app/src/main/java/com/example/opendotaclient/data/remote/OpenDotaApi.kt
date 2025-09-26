package com.example.opendotaclient.data.remote
import com.example.opendotaclient.data.model.PlayerProfile
import com.example.opendotaclient.data.model.RecentMatch

import retrofit2.http.GET
import retrofit2.http.Path

interface OpenDotaApi {
    @GET("players/{id}")
    suspend fun getPlayer(@Path("id") id: Int): PlayerProfile

    @GET("players/{id}/recentMatches")
    suspend fun getRecentMatches(@Path("id") id: Int): List<RecentMatch>
}

package com.example.opendotaclient.data

data class PlayerOverviewUI(
    val name: String,
    val avatar: String?,
    val wins: Int,
    val losses: Int,
    val winrate: Double,
    val rankTier: Int?,
    val leaderboardRank: Int?,
    val avgGpm: Int? = null,
    val avgXpm: Int? = null,
    val avgLastHits: Int? = null,
    val avgHeroDmg: Int? = null
)

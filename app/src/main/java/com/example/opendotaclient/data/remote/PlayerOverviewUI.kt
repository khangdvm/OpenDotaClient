package com.example.opendotaclient.data

import com.example.opendotaclient.data.remote.PlayerHeroDTO
import com.example.opendotaclient.data.remote.PlayerRankingDTO

data class PlayerOverviewUI(
    val name: String,
    val avatar: String?,
    val topHeroes: List<PlayerHeroDTO>,
    val rankings: List<PlayerRankingDTO>
)

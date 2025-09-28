package com.example.opendotaclient.data

import com.example.opendotaclient.data.remote.PlayerRankingDTO
import com.example.opendotaclient.data.remote.PlayerHeroStatDTO

data class PlayerOverviewUI(
    val name: String,
    val avatar: String?,
    val topHeroes: List<PlayerHeroStatDTO>,
    val rankings: List<PlayerRankingDTO>
)

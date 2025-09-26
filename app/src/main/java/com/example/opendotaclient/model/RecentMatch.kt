package com.example.opendotaclient.data.model

data class RecentMatch(
    val match_id: Long,
    val hero_id: Int,
    val kills: Int,
    val deaths: Int,
    val assists: Int,
    val duration: Int,
    val start_time: Long,
    val player_slot: Int,
    val radiant_win: Boolean?
)

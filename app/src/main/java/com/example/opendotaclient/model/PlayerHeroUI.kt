package com.example.opendotaclient.model

data class PlayerHeroUI(
    val heroId: Int,
    val name: String,
    val imageUrl: String?,
    val games: Int,
    val win: Int
) {
    val winRate: Float get() = if (games > 0) win * 100f / games else 0f
}

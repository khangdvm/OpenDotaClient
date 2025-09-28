package com.example.opendotaclient.data.remote

import com.squareup.moshi.Json

data class RecentMatchDTO(
    @Json(name = "match_id") val matchId: Long,
    @Json(name = "hero_id") val heroId: Int,
    @Json(name = "kills") val kills: Int?,
    @Json(name = "deaths") val deaths: Int?,
    @Json(name = "assists") val assists: Int?,
    @Json(name = "player_slot") val playerSlot: Int?,
    @Json(name = "radiant_win") val radiantWin: Boolean?,
    @Json(name = "duration") val duration: Int?,      // giây
    @Json(name = "start_time") val startTime: Long?,  // epoch
)

data class HeroDTO(
    val id: Int,
    val name: String, // "npc_dota_hero_axe"
    @Json(name = "localized_name") val localizedName: String
)

data class HeroStatDTO(
    val id: Int,
    val name: String,                       // "npc_dota_hero_axe"
    @Json(name = "localized_name") val localizedName: String,
    @Json(name = "primary_attr") val primaryAttr: String, // str/agi/int/universal
    @Json(name = "attack_type") val attackType: String,   // Melee/Ranged
    val roles: List<String>,
    val img: String                         // "/apps/dota2/images/dota_react/heroes/axe.png?"
)

/** TeamDTO — dùng nullable để map an toàn với OpenDota */
data class TeamDTO(
    val team_id: Int?,
    val name: String?,
    val tag: String?,
    val logo_url: String?,
    val rating: Double?,
    val wins: Int?,
    val losses: Int?,
)

data class PublicMatchDTO(
    @Json(name = "match_id") val matchId: Long,
    @Json(name = "start_time") val startTime: Long?,
    @Json(name = "duration") val duration: Int?,
    @Json(name = "radiant_win") val radiantWin: Boolean?,
    @Json(name = "avg_mmr") val avgMmr: Int?,
    @Json(name = "lobby_type") val lobbyType: Int?
)

data class PlayerDTO(
    val profile: PlayerProfile?
) {
    data class PlayerProfile(
        val account_id: Long?,
        val personaname: String?,
        val name: String?,
        val avatarfull: String?
    )
}

data class PlayerHeroStatDTO(
    @Json(name = "hero_id") val heroId: Int,
    val games: Int,
    val win: Int
)

data class PlayerRankingDTO(
    @Json(name = "hero_id") val heroId: Int,
    val rank: Int?,
    val score: Double?
)

// com/example/opendotaclient/data/remote/dto.kt

data class MatchDetailDTO(
    @Json(name = "match_id") val matchId: Long,
    val players: List<MatchPlayerDTO> = emptyList()
)

data class MatchPlayerDTO(
    @Json(name = "account_id") val accountId: Long?,
    @Json(name = "hero_id") val heroId: Int?,
    // THÊM 2 FIELD NÀY (nullable, an toàn):
    @Json(name = "isRadiant") val isRadiant: Boolean? = null,
    @Json(name = "player_slot") val playerSlot: Int? = null
)

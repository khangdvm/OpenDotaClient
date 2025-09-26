package com.example.opendotaclient.data.model

data class PlayerProfile(
    val profile: Profile?,
    val rank_tier: Int?,
    val mmr_estimate: MmrEstimate?
)

data class Profile(
    val account_id: Int?,
    val personaname: String?,
    val avatarfull: String?
)

data class MmrEstimate(
    val estimate: Int?
)

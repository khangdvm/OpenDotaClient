package com.example.opendotaclient.data

data class TeamUI(
    val id: Long,
    val name: String,
    val tag: String?,
    val logoUrl: String?,
    val rating: Double?,
    val wins: Int,
    val losses: Int,
)
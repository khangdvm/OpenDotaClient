package com.example.opendotaclient.data.remote

import com.example.opendotaclient.data.TeamUI  // <-- THÊM IMPORT NÀY

class TeamsRepository(private val api: OpenDotaService) {

    private fun toCdn(path: String?): String? =
        when {
            path.isNullOrBlank() -> null
            path.startsWith("http") -> path
            else -> "https://cdn.cloudflare.steamstatic.com$path"
        }

    suspend fun getTopTeams(limit: Int = 50, apiKey: String? = null): List<TeamUI> {
        val list: List<TeamDTO> = api.getTeams(apiKey)
        return list
            .sortedByDescending { it.rating ?: 0.0 }
            .take(limit)
            .map { it.toUI() }
    }

    private fun TeamDTO.toUI() = TeamUI(
        id = teamId?.toLong() ?: 0L,         // dùng camelCase đúng với dto.kt
        name = name ?: tag ?: "Unknown",
        tag = tag,
        logoUrl = toCdn(logoUrl),
        rating = rating,
        wins = wins ?: 0,
        losses = losses ?: 0
    )
}

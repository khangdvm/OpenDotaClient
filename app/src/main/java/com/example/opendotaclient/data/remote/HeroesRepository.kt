package com.example.opendotaclient.data.remote

class HeroesRepository(
    private val api: OpenDotaService
) {
    suspend fun getHeroes(apiKey: String? = null): List<HeroStatDTO> =
        api.getHeroStats(apiKey)
}

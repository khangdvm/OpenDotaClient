package com.example.opendotaclient.data.remote

class HeroesRepository(private val api: OpenDotaService) {

    private fun toCdn(path: String?): String? {
        if (path.isNullOrBlank()) return null
        return if (path.startsWith("http")) path
        else "https://cdn.cloudflare.steamstatic.com$path"
    }

    /** Trả về danh sách HeroUI đã map sẵn tên + ảnh */
    suspend fun getHeroes(apiKey: String?): List<HeroUI> =
        api.getHeroStats(apiKey).map { it.toUI() }

    private fun HeroStatDTO.toUI() = HeroUI(
        id = id,
        name = localizedName,
        primaryAttr = primaryAttr,
        attackType = attackType,
        roles = roles,
        imageUrl = toCdn(img)           // <— quan trọng: có imageUrl
    )
}

/** UI model cho hero (dùng khắp app) */
data class HeroUI(
    val id: Int,
    val name: String,
    val primaryAttr: String,   // str/agi/int/universal
    val attackType: String,    // Melee/Ranged
    val roles: List<String>,
    val imageUrl: String?      // <— thuộc tính cần thiết
)

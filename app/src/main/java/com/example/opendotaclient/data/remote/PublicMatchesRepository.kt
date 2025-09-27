package com.example.opendotaclient.data.remote

/**
 * Repository cho feed Public Matches
 */
class PublicMatchesRepository(private val api: OpenDotaService) {

    /**
     * /publicMatches
     * @param mmrDesc: 1 nếu muốn sắp xếp theo MMR giảm dần (optional)
     * @param lessThanMatchId: phân trang thủ công theo match_id (optional)
     */
    suspend fun getPublicMatches(
        mmrDesc: Int? = null,
        lessThanMatchId: Long? = null
    ): List<PublicMatchDTO> {
        return api.getPublicMatches(mmrDesc, lessThanMatchId)
    }
}

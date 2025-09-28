// app/src/main/java/com/example/opendotaclient/data/remote/PublicMatchesRepository.kt
package com.example.opendotaclient.data.remote

// Giữ data class nhẹ nếu nơi khác còn import (an toàn)
data class PublicMatchUI(
    val id: Long,
    val avgMmr: Int?,
    val durationSec: Int?,
    val radiantWin: Boolean?
)

// Placeholder: không còn dùng Repo này cho PublicFeed
class PublicMatchesRepository

package com.example.opendotaclient.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.opendotaclient.data.remote.HeroesRepository
import com.example.opendotaclient.data.remote.MatchDetailRepository

class MatchDetailVMFactory(
    private val matchId: Long,
    private val detailRepo: MatchDetailRepository,
    private val heroesRepo: HeroesRepository,
    private val apiKey: String? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(MatchDetailViewModel::class.java))
        return MatchDetailViewModel(
            matchId = matchId,
            detailRepo = detailRepo,
            heroesRepo = heroesRepo,
            apiKey = apiKey
        ) as T
    }
}

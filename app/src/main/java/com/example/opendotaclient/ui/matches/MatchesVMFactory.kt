package com.example.opendotaclient.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.opendotaclient.data.remote.HeroesRepository
import com.example.opendotaclient.data.remote.MatchesRepository

class MatchesVMFactory(
    private val repo: MatchesRepository,
    private val heroesRepo: HeroesRepository,
    private val accountId32: Long,
    private val apiKey: String?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MatchesViewModel(repo, heroesRepo, accountId32, apiKey) as T
    }
}

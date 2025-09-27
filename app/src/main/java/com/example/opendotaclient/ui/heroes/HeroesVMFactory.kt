package com.example.opendotaclient.ui.heroes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.opendotaclient.data.remote.HeroesRepository

class HeroesVMFactory(
    private val repo: HeroesRepository,
    private val apiKey: String? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HeroesViewModel(repo, apiKey) as T
    }
}

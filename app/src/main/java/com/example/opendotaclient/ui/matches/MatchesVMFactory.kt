package com.example.opendotaclient.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.opendotaclient.data.remote.MatchesRepository

class MatchesVMFactory(
    private val repo: MatchesRepository,
    private val accountId32: Long,
    private val apiKey: String? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MatchesViewModel::class.java)) {
            return MatchesViewModel(repo, accountId32, apiKey) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

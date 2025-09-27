package com.example.opendotaclient.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.opendotaclient.data.remote.PublicMatchesRepository

class PublicFeedVMFactory(private val repo: PublicMatchesRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PublicFeedViewModel::class.java)) {
            return PublicFeedViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

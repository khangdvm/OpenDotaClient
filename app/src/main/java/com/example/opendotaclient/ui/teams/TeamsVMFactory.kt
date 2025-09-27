package com.example.opendotaclient.ui.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.opendotaclient.data.remote.TeamsRepository

class TeamsVMFactory(
    private val repo: TeamsRepository,
    private val apiKey: String? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(TeamsViewModel::class.java))
        return TeamsViewModel(repo, apiKey) as T
    }
}

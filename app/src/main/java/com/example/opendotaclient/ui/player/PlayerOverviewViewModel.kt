package com.example.opendotaclient.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.opendotaclient.data.PlayerOverviewRepository
import com.example.opendotaclient.data.PlayerOverviewUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerOverviewViewModel(
    private val repo: PlayerOverviewRepository,
    private val id32: Long
) : ViewModel() {
    private val _state = MutableStateFlow<PlayerOverviewUI?>(null)
    val state: StateFlow<PlayerOverviewUI?> = _state

    init { refresh() }
    fun refresh() = viewModelScope.launch { _state.value = repo.load(id32) }
}

@Suppress("UNCHECKED_CAST")
class PlayerOverviewVMFactory(
    private val repo: PlayerOverviewRepository,
    private val id32: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerOverviewViewModel(repo, id32) as T
    }
}

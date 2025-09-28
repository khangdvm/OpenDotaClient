package com.example.opendotaclient.ui.heroes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opendotaclient.data.remote.HeroesRepository
import com.example.opendotaclient.data.remote.HeroUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HeroesViewModel(
    private val repo: HeroesRepository,
    private val apiKey: String?
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _items = MutableStateFlow<List<HeroUI>>(emptyList())
    val items: StateFlow<List<HeroUI>> = _items

    init { load() }

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                // repo.getHeroes() đã trả List<HeroUI>, không map toUI nữa
                val heroes = repo.getHeroes(apiKey)
                _items.value = heroes
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}

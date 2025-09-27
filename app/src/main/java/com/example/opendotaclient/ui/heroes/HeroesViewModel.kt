package com.example.opendotaclient.ui.heroes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opendotaclient.data.remote.HeroesRepository
import com.example.opendotaclient.data.remote.HeroStatDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HeroUI(
    val id: Int,
    val name: String,
    val primaryAttr: String,
    val attackType: String,
    val roles: List<String>,
    val imageUrl: String
)

class HeroesViewModel(
    private val repo: HeroesRepository,
    private val apiKey: String? = null
) : ViewModel() {

    private val _items = MutableStateFlow<List<HeroUI>>(emptyList())
    val items: StateFlow<List<HeroUI>> = _items

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val stats = repo.getHeroes(apiKey)
                _items.value = stats.map { it.toUI() }
            } finally {
                _loading.value = false
            }
        }
    }

    private fun HeroStatDTO.toUI(): HeroUI {
        return HeroUI(
            id = id,
            name = localizedName,
            primaryAttr = primaryAttr,
            attackType = attackType,
            roles = roles,
            imageUrl = toCdn(img)
        )
    }

    private fun toCdn(path: String): String {
        return if (path.startsWith("http")) path
        else "https://cdn.cloudflare.steamstatic.com$path"
    }
}

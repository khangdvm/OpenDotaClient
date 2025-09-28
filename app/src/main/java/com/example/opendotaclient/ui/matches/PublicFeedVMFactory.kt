// app/src/main/java/com/example/opendotaclient/ui/matches/PublicFeedVMFactory.kt
package com.example.opendotaclient.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PublicFeedVMFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PublicFeedViewModel() as T
    }
}

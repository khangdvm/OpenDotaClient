@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.opendotaclient.ui.matches
import androidx.compose.material3.ExperimentalMaterial3Api
// ...



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MatchesScreen(vm: MatchesViewModel) {
    val loading by vm.loading.collectAsState()
    val items by vm.items.collectAsState()
    val error by vm.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recent Matches") },
                actions = {
                    IconButton(onClick = { vm.load() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { inner ->
        Box(Modifier.fillMaxSize().padding(inner)) {
            when {
                loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                error != null -> Text(error ?: "", Modifier.align(Alignment.Center))
                else -> LazyColumn(
                    contentPadding = PaddingValues(all = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items) { m -> MatchRow(m) }
                }
            }
        }
    }
}

@Composable
private fun MatchRow(m: RecentMatchUI) {
    ElevatedCard {
        Column(Modifier.padding(12.dp)) {
            Text("Match #${m.matchId}")
            Spacer(Modifier.height(4.dp))
            Text("Hero ID: ${m.heroId ?: "-"} • K/D/A: ${m.k ?: 0}/${m.d ?: 0}/${m.a ?: 0}")
            Spacer(Modifier.height(4.dp))
            val dur = (m.duration ?: 0)
            val mins = dur / 60
            val secs = dur % 60
            val result = when (m.didWin) { true -> "WIN"; false -> "LOSS"; else -> "N/A" }
            Text("Duration: %d:%02d • Result: %s".format(mins, secs, result))
        }
    }
}

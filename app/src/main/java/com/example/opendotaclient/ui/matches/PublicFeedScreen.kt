@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.opendotaclient.ui.matches

import androidx.compose.foundation.clickable
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
fun PublicFeedScreen(
    vm: PublicFeedViewModel,
    onOpenMatch: (Long) -> Unit = {}   // <- callback mở chi tiết
) {
    val loading by vm.loading.collectAsState()
    val items by vm.items.collectAsState()
    val error by vm.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Public Matches") },
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
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items) { m ->
                        PublicMatchRow(
                            m = m,
                            onClick = { onOpenMatch(m.matchId) } // <- bấm để mở chi tiết
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PublicMatchRow(
    m: PublicFeedItemUI,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // <- cho phép bấm
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("Match #${m.matchId}")
            val dur = (m.duration ?: 0)
            val mins = dur / 60
            val secs = dur % 60
            Text("Avg MMR: ${m.avgMmr ?: "-"} • Duration: %d:%02d".format(mins, secs))
            Text(
                "Result: " + when (m.radiantWin) {
                    true -> "Radiant Win"
                    false -> "Dire Win"
                    else -> "N/A"
                }
            )
        }
    }
}

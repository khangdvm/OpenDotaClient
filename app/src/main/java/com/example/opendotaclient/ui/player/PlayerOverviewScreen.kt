package com.example.opendotaclient.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.opendotaclient.data.PlayerOverviewUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerOverviewScreen(vm: PlayerOverviewViewModel, onOpenMatches: () -> Unit) {
    val s: PlayerOverviewUI? by vm.state.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Player Overview") }) }) { pad ->
        if (s == null) {
            Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(Modifier.padding(pad).padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(model = s!!.avatar, contentDescription = null, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(s!!.name, style = MaterialTheme.typography.titleLarge)
                }
                Spacer(Modifier.height(16.dp))

                var tab by remember { mutableStateOf(0) }
                val tabs = listOf("Overview", "Matches", "Heroes", "Rankings")
                TabRow(selectedTabIndex = tab) {
                    tabs.forEachIndexed { i, t -> Tab(selected = tab==i, onClick = { tab = i }, text = { Text(t) }) }
                }
                Spacer(Modifier.height(12.dp))

                when (tab) {
                    0 -> {
                        Text("Top heroes:")
                        s!!.topHeroes.forEach { Text("• Hero ${it.heroId}: ${it.win}/${it.games}") }
                    }
                    1 -> onOpenMatches()
                    2 -> s!!.topHeroes.forEach { Text("• Hero ${it.heroId}: ${it.games} games") }
                    3 -> s!!.rankings.forEach { Text("• Hero ${it.heroId}: rank ${it.rank ?: "-"}") }
                }
            }
        }
    }
}

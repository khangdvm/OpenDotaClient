package com.example.opendotaclient.ui.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.opendotaclient.data.remote.OpenDotaService
import com.example.opendotaclient.data.remote.RetrofitClient
import androidx.compose.material3.ExperimentalMaterial3Api
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicFeedScreen(
    vm: PublicFeedViewModel,
    onOpenMatch: (Long) -> Unit
) {
    val items by vm.items.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(Unit) { vm.load() }

    val api: OpenDotaService = remember { RetrofitClient.api }

    var heroIconMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    LaunchedEffect(Unit) {
        runCatching {
            val heroes = api.getHeroStats(null)
            heroIconMap = heroes.associate { dto -> dto.id to toCdn(dto.img) }
        }
    }

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
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when {
                loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: $error")
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items) { match ->
                        val matchId = match.id

                        var radiant by remember(matchId) { mutableStateOf<List<Int>>(emptyList()) }
                        var dire by remember(matchId) { mutableStateOf<List<Int>>(emptyList()) }

                        LaunchedEffect(matchId) {
                            runCatching {
                                val detail = api.getMatch(matchId)

                                val (radiantPlayers, direPlayers) = detail.players.partition { p ->
                                    when {
                                        p.isRadiant == true -> true
                                        p.isRadiant == false -> false
                                        p.playerSlot != null -> p.playerSlot < 128
                                        else -> true // fallback Radiant
                                    }
                                }

                                radiant = radiantPlayers.mapNotNull { it.heroId }.take(5)
                                dire = direPlayers.mapNotNull { it.heroId }.take(5)
                            }
                        }

                        PublicMatchItem(
                            title = "Match #$matchId",
                            avgMmrText = (match.avgMmr ?: "-").toString(),
                            durationText = formatDurationNullable(match.durationSec),
                            resultText = when (match.radiantWin) {
                                true -> "Radiant Win"
                                false -> "Dire Win"
                                null -> "-"
                            },
                            radiantHeroes = radiant,
                            direHeroes = dire,
                            heroIconMap = heroIconMap,
                            onClick = { onOpenMatch(matchId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PublicMatchItem(
    title: String,
    avgMmrText: String,
    durationText: String,
    resultText: String,
    radiantHeroes: List<Int>,
    direHeroes: List<Int>,
    heroIconMap: Map<Int, String>,
    onClick: () -> Unit
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text("Avg MMR: $avgMmrText • Duration: $durationText")
            Text("Result: $resultText")

            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                HeroRow(radiantHeroes, heroIconMap, Modifier.weight(1f))
                Text("vs", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 8.dp))
                HeroRow(direHeroes, heroIconMap, Modifier.weight(1f), endAlign = true)
            }
        }
    }
}

@Composable
private fun HeroRow(
    heroIds: List<Int>,
    iconMap: Map<Int, String>,
    modifier: Modifier = Modifier,
    endAlign: Boolean = false
) {
    Row(
        modifier = modifier,
        horizontalArrangement = if (endAlign) Arrangement.End else Arrangement.Start
    ) {
        heroIds.forEachIndexed { idx, id ->
            val url = iconMap[id]
            Box(Modifier.padding(end = if (idx < heroIds.lastIndex) 6.dp else 0.dp)) {
                if (url != null) {
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp).clip(RoundedCornerShape(4.dp))
                    )
                } else {
                    Box(
                        Modifier.size(24.dp).clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }
        }
    }
}

private fun toCdn(path: String): String =
    if (path.startsWith("http")) path else "https://cdn.cloudflare.steamstatic.com$path"

private fun formatDurationNullable(totalSec: Int?): String {
    if (totalSec == null) return "-"
    val m = totalSec / 60
    val s = totalSec % 60
    return "%d:%02d".format(m, s)
}

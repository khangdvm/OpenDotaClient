@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.opendotaclient.ui.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*  // <-- đảm bảo import material3 TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.opendotaclient.data.remote.RecentMatchDTO   // 👈 dùng DTO trực tiếp
import com.example.opendotaclient.model.PlayerHeroUI

@Composable
fun MatchesScreen(vm: MatchesViewModel) {
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val section by vm.section.collectAsState()
    val recent by vm.recent.collectAsState()          // List<RecentMatchDTO> (khuyến nghị)
    val heroes by vm.heroesPlayed.collectAsState()

    LaunchedEffect(Unit) { vm.load() }

    Scaffold(
        topBar = {
            // ❌ SmallTopAppBar -> ✅ TopAppBar
            TopAppBar(
                title = { Text("Player Overview") },
                actions = {
                    IconButton(onClick = { vm.load() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(12.dp)
        ) {
            var expanded by remember { mutableStateOf(false) }
            val options = listOf("Recent Matches", "Heroes Played")
            val selectedLabel = when (section) {
                PlayerSection.RECENT_MATCHES -> options[0]
                PlayerSection.HEROES_PLAYED -> options[1]
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Section") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text(options[0]) },
                        onClick = { vm.setSection(PlayerSection.RECENT_MATCHES); expanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text(options[1]) },
                        onClick = { vm.setSection(PlayerSection.HEROES_PLAYED); expanded = false }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            error?.let {
                Text("Error: $it", color = Color.Red)
                return@Column
            }

            when (section) {
                PlayerSection.RECENT_MATCHES -> {
                    RecentMatchesList(items = recent)   // ⬅️ có hàm bên dưới
                }
                PlayerSection.HEROES_PLAYED -> {
                    HeroesPlayedList(items = heroes)
                }
            }
        }
    }
}

/* ================== RECENT MATCHES LIST (đơn giản) ================== */

@Composable
private fun RecentMatchesList(items: List<RecentMatchDTO>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(items) { m ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF132631),
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    Text("Match #${m.matchId}", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    val k = m.kills ?: 0
                    val d = m.deaths ?: 0
                    val a = m.assists ?: 0
                    Text(
                        "Hero ID: ${m.heroId} • K/D/A: $k/$d/$a",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFBDD1DE)
                    )
                    Spacer(Modifier.height(2.dp))
                    val dur = (m.duration ?: 0)
                    val mm = dur / 60
                    val ss = dur % 60
                    val win = when (m.radiantWin) {
                        true -> "WIN"
                        false -> "LOSS"
                        else -> "—"
                    }
                    Text(
                        "Duration: %d:%02d • Result: %s".format(mm, ss, win),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFBDD1DE)
                    )
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

/* ================== HEROES PLAYED LIST (như đã gửi) ================== */

@Composable
fun HeroesPlayedList(items: List<PlayerHeroUI>) {
    if (items.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hero stats yet")
        }
        return
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(items) { h -> HeroRow(h) }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun HeroRow(h: PlayerHeroUI) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF132631),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)).background(Color(0x22FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Text(h.name.take(1).uppercase(), color = Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(h.name, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Games: ${h.games}  •  Wins: ${h.win}  •  WR: ${"%.1f".format(h.winRate)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFBDD1DE)
                )
                Spacer(Modifier.height(8.dp))
                WinRateBar(progress = (h.winRate / 100f).coerceIn(0f, 1f))
            }
        }
    }
}

@Composable
private fun WinRateBar(progress: Float) {
    Box(
        Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(100)).background(Color(0xFF0F2230))
    ) {
        Box(
            Modifier.fillMaxHeight().fillMaxWidth(progress).background(Color(0xFF3BA1E6))
        )
    }
}

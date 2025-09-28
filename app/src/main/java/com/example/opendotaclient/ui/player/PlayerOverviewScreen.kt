@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.opendotaclient.ui.player

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.opendotaclient.data.PlayerOverviewUI
import kotlin.math.min

// Phù hợp Material3 version cũ
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults

@Composable
fun PlayerOverviewScreen(vm: PlayerOverviewViewModel) {
    val st by vm.state.collectAsState()
    LaunchedEffect(Unit) { vm.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("<OPENDOTA/>") },
                actions = { IconButton(onClick = vm::load) { Icon(Icons.Filled.Refresh, null) } }
            )
        }
    ) { inner ->
        Box(Modifier.padding(inner).fillMaxSize()) {
            when {
                st.loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                st.error != null -> Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(st.error ?: "", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = vm::load) { Text("Retry") }
                }
                else -> PlayerContent(st, vm::setSection)
            }
        }
    }
}

@Composable
private fun PlayerContent(state: PlayerState, onSectionChange: (PlayerSection) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val label = when (state.section) {
        PlayerSection.OVERVIEW -> "Overview"
        PlayerSection.RECENT_MATCHES -> "Recent Matches"
        PlayerSection.HEROES_PLAYED -> "Heroes Played"
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = label,
                onValueChange = {},
                readOnly = true,
                label = { Text("Section") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor() // để tương thích version M3 cũ
                    .fillMaxWidth(),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text("Overview") },
                    onClick = { expanded = false; onSectionChange(PlayerSection.OVERVIEW) }
                )
                DropdownMenuItem(
                    text = { Text("Recent Matches") },
                    onClick = { expanded = false; onSectionChange(PlayerSection.RECENT_MATCHES) }
                )
                DropdownMenuItem(
                    text = { Text("Heroes Played") },
                    onClick = { expanded = false; onSectionChange(PlayerSection.HEROES_PLAYED) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        when (state.section) {
            PlayerSection.OVERVIEW -> OverviewSection(
                ov = state.overview,
                rankIcon = state.rankIcon,
                dire = state.dire,
                radiant = state.radiant
            )
            PlayerSection.RECENT_MATCHES -> RecentMatchesList(state.recentMatches)
            PlayerSection.HEROES_PLAYED -> HeroesPlayedList(state.heroesPlayed)
        }
    }
}

@Composable
private fun OverviewSection(
    ov: PlayerOverviewUI?,
    rankIcon: String?,
    dire: SideWinUI?,
    radiant: SideWinUI?
) {
    if (ov == null) return
    val ctx = LocalContext.current

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ImageRequest.Builder(ctx).data(ov.avatar).crossfade(true).build(),
                contentDescription = "Avatar",
                modifier = Modifier.size(56.dp).clip(CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(ov.name, style = MaterialTheme.typography.titleLarge)
                Row {
                    AssistChip(onClick = {}, label = { Text("Wins ${ov.wins}") })
                    Spacer(Modifier.width(8.dp))
                    AssistChip(onClick = {}, label = { Text("Losses ${ov.losses}") })
                    Spacer(Modifier.width(8.dp))
                    AssistChip(onClick = {}, label = { Text(String.format("WR %.1f%%", ov.winrate)) })
                }
            }
            if (rankIcon != null) {
                AsyncImage(
                    model = ImageRequest.Builder(ctx).data(rankIcon).crossfade(true).build(),
                    contentDescription = "Rank",
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        if (ov.rankTier != null || ov.leaderboardRank != null) {
            Spacer(Modifier.height(12.dp))
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("Rank", style = MaterialTheme.typography.titleMedium)
                    ov.rankTier?.let { Text("Rank Tier: $it") }
                    ov.leaderboardRank?.let { Text("Leaderboard: #$it") }
                }
            }
        }

        if (ov.avgGpm != null || ov.avgXpm != null || ov.avgLastHits != null || ov.avgHeroDmg != null) {
            Spacer(Modifier.height(12.dp))
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("Averages (Recent)", style = MaterialTheme.typography.titleMedium)
                    ov.avgGpm?.let { Text("GPM: $it") }
                    ov.avgXpm?.let { Text("XPM: $it") }
                    ov.avgLastHits?.let { Text("Last Hits: $it") }
                    ov.avgHeroDmg?.let { Text("Hero Damage: $it") }
                }
            }
        }

        if (dire != null || radiant != null) {
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                dire?.let { DonutCard(it, modifier = Modifier.weight(1f)) }
                radiant?.let { DonutCard(it, modifier = Modifier.weight(1f)) }
            }
        }
    }
}

/* -------- Donut Chart ------- */

@Composable
private fun DonutCard(data: SideWinUI, modifier: Modifier = Modifier) {
    ElevatedCard(modifier) {
        Column(
            Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(data.label, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            DonutChart(percentage = data.winRate, diameterDp = 96.dp, stroke = 14.dp)
            Spacer(Modifier.height(6.dp))
            Text("${data.winRate}% winrate")
            Text("W ${data.wins} / L ${data.losses}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun DonutChart(percentage: Int, diameterDp: Dp, stroke: Dp) {
    val pct = percentage.coerceIn(0, 100)

    // ❗ LẤY MÀU NGOÀI CANVAS (tránh gọi composable trong draw scope)
    val bgColor = MaterialTheme.colorScheme.surfaceVariant
    val fgColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = Modifier.size(diameterDp)) {
        val sweep = 360f * pct / 100f

        // size của DrawScope là PX
        val canvasSize = this.size
        val diameter = min(canvasSize.width, canvasSize.height)
        val topLeft = Offset(
            (canvasSize.width - diameter) / 2f,
            (canvasSize.height - diameter) / 2f
        )
        val arcSize = Size(diameter, diameter)

        // background ring
        drawArc(
            color = bgColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke.toPx(), cap = StrokeCap.Round)
        )
        // progress ring
        drawArc(
            color = fgColor,
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke.toPx(), cap = StrokeCap.Round)
        )
    }
}

/* ---------- Lists (đã có ảnh + tên hero) ---------- */

@Composable
private fun RecentMatchesList(items: List<RecentMatchRowUI>) {
    val ctx = LocalContext.current
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(items) { m ->
            ElevatedCard(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = ImageRequest.Builder(ctx).data(m.heroImg).crossfade(true).build(),
                        contentDescription = m.heroName,
                        modifier = Modifier.size(48.dp).clip(CircleShape)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(m.heroName, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(2.dp))
                        Text("K/D/A: ${m.k}/${m.d}/${m.a}")
                        Text("Duration: ${formatDuration(m.durationSec)}  •  Result: ${m.result}")
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroesPlayedList(items: List<HeroPlayedRowUI>) {
    val ctx = LocalContext.current
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(items) { h ->
            ElevatedCard(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = ImageRequest.Builder(ctx).data(h.heroImg).crossfade(true).build(),
                        contentDescription = h.heroName,
                        modifier = Modifier.size(48.dp).clip(CircleShape)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(h.heroName, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(2.dp))
                        Text("Games: ${h.games} • Wins: ${h.wins} • WR: ${h.winRate}%")
                    }
                }
            }
        }
    }
}

private fun formatDuration(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    return "%d:%02d".format(m, s)
}

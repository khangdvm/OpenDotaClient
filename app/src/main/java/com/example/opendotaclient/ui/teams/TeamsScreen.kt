package com.example.opendotaclient.ui.teams

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.opendotaclient.data.TeamUI

@Composable
fun TeamsScreen(viewModel: TeamsViewModel) {
    // lấy state từ VM
    val state by viewModel.state.collectAsState()

    // load data khi vào màn
    LaunchedEffect(Unit) {
        // nếu VM của bạn đặt tên khác (vd: loadTeams), đổi lại ở đây
        viewModel.load(limit = 50)
    }

    when {
        state.loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        state.error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Load teams failed: ${state.error}")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.load(limit = 50) }) {
                        Text("Retry")
                    }
                }
            }
        }
        else -> {
            TeamsList(items = state.items)
        }
    }
}

@Composable
private fun TeamsList(items: List<TeamUI>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items, key = { it.id }) { team ->
            TeamRow(team)
        }
    }
}

@Composable
private fun TeamRow(team: TeamUI) {
    ElevatedCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = team.logoUrl,
                contentDescription = team.name,
                modifier = Modifier.size(56.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = team.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                val subtitle = buildString {
                    if (!team.tag.isNullOrBlank()) append("[${team.tag}]  ")
                    append("Rating: ${team.rating ?: 0.0}")
                }
                Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("W ${team.wins}", style = MaterialTheme.typography.bodyMedium)
                Text("L ${team.losses}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

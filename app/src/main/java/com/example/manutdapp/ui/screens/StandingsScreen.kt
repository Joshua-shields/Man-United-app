package com.example.manutdapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manutdapp.data.TeamStanding
import com.example.manutdapp.ui.ManUtdViewModel
import com.example.manutdapp.ui.theme.UtdGold
import com.example.manutdapp.ui.theme.UtdRed

@Composable
fun StandingsScreen(viewModel: ManUtdViewModel) {
    val standings by viewModel.standings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "#", modifier = Modifier.width(30.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(text = "Team", modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(text = "P", modifier = Modifier.width(30.dp), textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(text = "GD", modifier = Modifier.width(30.dp), textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(text = "Pts", modifier = Modifier.width(40.dp), textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        }
        Divider(color = Color.LightGray.copy(alpha = 0.5f))

        LazyColumn {
            items(standings) { team ->
                StandingRow(team = team)
                Divider(color = Color.LightGray.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
fun StandingRow(team: TeamStanding) {
    val isUtd = team.name == "Man United"
    val backgroundColor = if (isUtd) UtdRed.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${team.position}",
            modifier = Modifier.width(30.dp),
            fontWeight = if (isUtd) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isUtd) {
                 Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(UtdRed))
                 Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = team.name,
                fontWeight = if (isUtd) FontWeight.Bold else FontWeight.Normal,
                color = if (isUtd) UtdRed else MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = "${team.played}",
            modifier = Modifier.width(30.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "${team.goalDifference}",
            modifier = Modifier.width(30.dp),
            textAlign = TextAlign.Center,
            color = if (team.goalDifference > 0) Color(0xFF2E7D32) else if (team.goalDifference < 0) Color.Red else Color.Gray
        )
        Text(
            text = "${team.points}",
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

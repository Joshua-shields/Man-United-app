package com.example.manutdapp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.manutdapp.data.Match
import com.example.manutdapp.ui.ManUtdViewModel
import com.example.manutdapp.ui.theme.UtdBlack
import com.example.manutdapp.ui.theme.UtdGold
import com.example.manutdapp.ui.theme.UtdRed

@Composable
fun MatchesScreen(viewModel: ManUtdViewModel) {
    val upcoming by viewModel.upcomingMatches.collectAsState()
    val recent by viewModel.recentResults.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "NEXT MATCH",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (upcoming.isNotEmpty()) {
                NextMatchCard(match = upcoming.first())
            }
        }

        item {
            Text(
                text = "UPCOMING",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }
        
        items(upcoming.drop(1)) { match ->
            MatchRow(match)
        }

        item {
            Text(
                text = "RECENT RESULTS",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        items(recent) { match ->
            MatchRow(match)
        }
    }
}

@Composable
fun NextMatchCard(match: Match) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(UtdRed, Color(0xFF800000))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = match.competition.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = UtdGold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TeamLogo(name = match.homeTeam, isBig = true)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = match.time,
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = match.date,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    TeamLogo(name = match.awayTeam, isBig = true)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { 
                        // Open the Manchester United official fixtures page
                        val url = "https://www.manutd.com/en/fixtures"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UtdGold),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    Text(text = "MATCH CENTER", color = UtdBlack, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MatchRow(match: Match) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.width(60.dp)) {
                    Text(
                        text = match.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TeamLogo(name = match.homeTeam, size = 20)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = match.homeTeam, fontWeight = if (match.homeTeam == "Man United") FontWeight.Bold else FontWeight.Normal)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TeamLogo(name = match.awayTeam, size = 20)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = match.awayTeam, fontWeight = if (match.awayTeam == "Man United") FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
            
            if (match.homeScore != null && match.awayScore != null) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${match.homeScore}",
                        fontWeight = FontWeight.Bold,
                        color = if (match.homeScore > match.awayScore) UtdRed else Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${match.awayScore}",
                        fontWeight = FontWeight.Bold,
                        color = if (match.awayScore > match.homeScore) UtdRed else Color.Black
                    )
                }
            } else {
                Text(
                    text = match.time,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TeamLogo(name: String, isBig: Boolean = false, size: Int = 40) {
    val displaySize = if (isBig) 64.dp else size.dp
    
    // Map team names to their crest URLs
    val logoUrl = getTeamLogoUrl(name)
    
    Box(
        modifier = Modifier
            .size(displaySize)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = logoUrl,
            contentDescription = "$name logo",
            modifier = Modifier
                .size(displaySize * 0.8f)
                .clip(CircleShape),
            contentScale = ContentScale.Fit
        )
    }
}

// Helper function to get team logo URLs from crests.football-data.org
fun getTeamLogoUrl(teamName: String): String {
    return when {
        teamName.contains("Man United", ignoreCase = true) || 
        teamName.contains("Manchester United", ignoreCase = true) -> 
            "https://crests.football-data.org/66.png"
        
        teamName.contains("Arsenal", ignoreCase = true) -> 
            "https://crests.football-data.org/57.png"
        
        teamName.contains("Liverpool", ignoreCase = true) -> 
            "https://crests.football-data.org/64.png"
        
        teamName.contains("Chelsea", ignoreCase = true) -> 
            "https://crests.football-data.org/61.png"
        
        teamName.contains("Man City", ignoreCase = true) || 
        teamName.contains("Manchester City", ignoreCase = true) -> 
            "https://crests.football-data.org/65.png"
        
        teamName.contains("Tottenham", ignoreCase = true) || 
        teamName.contains("Spurs", ignoreCase = true) -> 
            "https://crests.football-data.org/73.png"
        
        teamName.contains("Newcastle", ignoreCase = true) -> 
            "https://crests.football-data.org/67.png"
        
        teamName.contains("Aston Villa", ignoreCase = true) -> 
            "https://crests.football-data.org/58.png"
        
        teamName.contains("Brighton", ignoreCase = true) -> 
            "https://crests.football-data.org/397.png"
        
        teamName.contains("West Ham", ignoreCase = true) -> 
            "https://crests.football-data.org/563.png"
        
        teamName.contains("Everton", ignoreCase = true) -> 
            "https://crests.football-data.org/62.png"
        
        teamName.contains("Brentford", ignoreCase = true) -> 
            "https://crests.football-data.org/402.png"
        
        teamName.contains("Fulham", ignoreCase = true) -> 
            "https://crests.football-data.org/63.png"
        
        teamName.contains("Crystal Palace", ignoreCase = true) -> 
            "https://crests.football-data.org/354.png"
        
        teamName.contains("Wolves", ignoreCase = true) || 
        teamName.contains("Wolverhampton", ignoreCase = true) -> 
            "https://crests.football-data.org/76.png"
        
        teamName.contains("Bournemouth", ignoreCase = true) -> 
            "https://crests.football-data.org/1044.png"
        
        teamName.contains("Nottingham", ignoreCase = true) || 
        teamName.contains("Nott'm Forest", ignoreCase = true) -> 
            "https://crests.football-data.org/351.png"
        
        teamName.contains("Leicester", ignoreCase = true) -> 
            "https://crests.football-data.org/338.png"
        
        teamName.contains("Leeds", ignoreCase = true) -> 
            "https://crests.football-data.org/341.png"
        
        teamName.contains("Southampton", ignoreCase = true) -> 
            "https://crests.football-data.org/340.png"
        
        teamName.contains("Ipswich", ignoreCase = true) -> 
            "https://crests.football-data.org/349.png"
        
        // European teams for UCL/UEL matches
        teamName.contains("Barcelona", ignoreCase = true) -> 
            "https://crests.football-data.org/81.png"
        
        teamName.contains("Real Madrid", ignoreCase = true) -> 
            "https://crests.football-data.org/86.png"
        
        teamName.contains("Bayern", ignoreCase = true) -> 
            "https://crests.football-data.org/5.png"
        
        teamName.contains("PSG", ignoreCase = true) || 
        teamName.contains("Paris", ignoreCase = true) -> 
            "https://crests.football-data.org/524.png"
        
        teamName.contains("Juventus", ignoreCase = true) -> 
            "https://crests.football-data.org/109.png"
        
        teamName.contains("Inter", ignoreCase = true) && 
        !teamName.contains("United", ignoreCase = true) -> 
            "https://crests.football-data.org/108.png"
        
        teamName.contains("Milan", ignoreCase = true) && 
        !teamName.contains("Inter", ignoreCase = true) -> 
            "https://crests.football-data.org/98.png"
        
        teamName.contains("Atletico", ignoreCase = true) -> 
            "https://crests.football-data.org/78.png"
        
        teamName.contains("Galatasaray", ignoreCase = true) -> 
            "https://crests.football-data.org/610.png"
        
        teamName.contains("Porto", ignoreCase = true) -> 
            "https://crests.football-data.org/503.png"
        
        teamName.contains("Copenhagen", ignoreCase = true) -> 
            "https://crests.football-data.org/263.png"
        
        else -> "https://crests.football-data.org/1.png" // Generic football icon
    }
}

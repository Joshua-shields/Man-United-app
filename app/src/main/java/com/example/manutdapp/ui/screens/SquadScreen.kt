package com.example.manutdapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.manutdapp.data.Player
import com.example.manutdapp.ui.ManUtdViewModel
import com.example.manutdapp.ui.theme.UtdBlack
import com.example.manutdapp.ui.theme.UtdGold
import com.example.manutdapp.ui.theme.UtdRed

@Composable
fun SquadScreen(viewModel: ManUtdViewModel) {
    val squad by viewModel.squad.collectAsState()
    
    // Group players by position
    val goalkeepers = squad.filter { it.position == "Goalkeeper" }
    val defenders = squad.filter { it.position == "Defence" }
    val midfielders = squad.filter { it.position == "Midfield" }
    val attackers = squad.filter { it.position == "Offence" || it.position == "Attack" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = UtdRed)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(UtdRed, Color(0xFF800000))
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "MANCHESTER UNITED",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "SQUAD 2024/25",
                            style = MaterialTheme.typography.bodyLarge,
                            color = UtdGold
                        )
                    }
                }
            }
        }
        
        // Goalkeepers
        if (goalkeepers.isNotEmpty()) {
            item {
                PositionHeader("GOALKEEPERS")
            }
            items(goalkeepers) { player ->
                PlayerCard(player)
            }
        }
        
        // Defenders
        if (defenders.isNotEmpty()) {
            item {
                PositionHeader("DEFENDERS")
            }
            items(defenders) { player ->
                PlayerCard(player)
            }
        }
        
        // Midfielders
        if (midfielders.isNotEmpty()) {
            item {
                PositionHeader("MIDFIELDERS")
            }
            items(midfielders) { player ->
                PlayerCard(player)
            }
        }
        
        // Attackers
        if (attackers.isNotEmpty()) {
            item {
                PositionHeader("FORWARDS")
            }
            items(attackers) { player ->
                PlayerCard(player)
            }
        }
    }
}

@Composable
fun PositionHeader(position: String) {
    Text(
        text = position,
        style = MaterialTheme.typography.titleMedium,
        color = UtdRed,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun PlayerCard(player: Player) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player Photo with Number Badge
            Box(
                modifier = Modifier.size(76.dp)
            ) {
                // Photo background with fallback initials
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5))
                        .border(2.dp, UtdRed, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    SubcomposeAsyncImage(
                        model = player.photoUrl,
                        contentDescription = "${player.name} photo",
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        loading = {
                            // Show initials while loading
                            PlayerInitials(player.name)
                        },
                        error = {
                            // Show initials if image fails to load
                            PlayerInitials(player.name)
                        }
                    )
                }
                
                // Shirt number badge
                if (player.shirtNumber != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(UtdRed)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = player.shirtNumber.toString(),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Player Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = player.position,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = getFlagEmoji(player.nationality),
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = player.nationality,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerInitials(name: String) {
    val initials = name.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
    
    Box(
        modifier = Modifier
            .size(70.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(UtdRed, Color(0xFF800000))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

// Helper function to get flag emoji from country name
fun getFlagEmoji(countryName: String): String {
    return when (countryName) {
        "England" -> "🏴󠁧󠁢󠁥󠁮󠁧󠁿"
        "Scotland" -> "🏴󠁧󠁢󠁳󠁣󠁴󠁿"
        "Wales" -> "🏴󠁧󠁢󠁷󠁬󠁳󠁿"
        "Republic of Ireland", "Ireland" -> "🇮🇪"
        "France" -> "🇫🇷"
        "Spain" -> "🇪🇸"
        "Portugal" -> "🇵🇹"
        "Brazil" -> "🇧🇷"
        "Argentina" -> "🇦🇷"
        "Uruguay" -> "🇺🇾"
        "Netherlands" -> "🇳🇱"
        "Belgium" -> "🇧🇪"
        "Sweden" -> "🇸🇪"
        "Denmark" -> "🇩🇰"
        "Norway" -> "🇳🇴"
        "Ivory Coast" -> "🇨🇮"
        "Cameroon" -> "🇨🇲"
        "Serbia" -> "🇷🇸"
        "Turkey" -> "🇹🇷"
        "Germany" -> "🇩🇪"
        "Italy" -> "🇮🇹"
        else -> "🌍"
    }
}

package com.example.manutdapp.data

import com.google.gson.annotations.SerializedName

// --- UI Models (Keep these clean for the View) ---
data class Match(
    val id: Int,
    val homeTeam: String,
    val awayTeam: String,
    val date: String,
    val time: String,
    val status: MatchStatus,
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val competition: String = "Premier League"
)

enum class MatchStatus {
    UPCOMING, LIVE, FINISHED
}

data class TeamStanding(
    val position: Int,
    val name: String,
    val played: Int,
    val won: Int,
    val drawn: Int,
    val lost: Int,
    val goalDifference: Int,
    val points: Int
)

data class NewsItem(
    val id: Int,
    val title: String,
    val summary: String,
    val imageUrl: String?,
    val date: String
)

data class Player(
    val id: Int,
    val name: String,
    val position: String,
    val shirtNumber: Int?,
    val nationality: String,
    val dateOfBirth: String?,
    val photoUrl: String?
)

// --- API Response Models (Matches JSON Structure) ---

data class ApiMatchesResponse(
    val matches: List<ApiMatch>
)

data class ApiMatch(
    val id: Int,
    val utcDate: String,
    val status: String,
    val homeTeam: ApiTeam,
    val awayTeam: ApiTeam,
    val score: ApiScore,
    val competition: ApiCompetition
)

data class ApiTeam(
    val name: String,
    val shortName: String?
)

data class ApiScore(
    val fullTime: ApiScoreDetail
)

data class ApiScoreDetail(
    val home: Int?,
    val away: Int?
)

data class ApiCompetition(
    val name: String
)

data class ApiStandingsResponse(
    val standings: List<ApiStandingTable>
)

data class ApiStandingTable(
    val type: String,
    val table: List<ApiTablePosition>
)

data class ApiTablePosition(
    val position: Int,
    val team: ApiTeam,
    val playedGames: Int,
    val won: Int,
    val draw: Int,
    val lost: Int,
    val points: Int,
    val goalDifference: Int
)

data class ApiSquadResponse(
    val squad: List<ApiPlayer>
)

data class ApiPlayer(
    val id: Int,
    val name: String,
    val position: String,
    val dateOfBirth: String?,
    val nationality: String,
    val shirtNumber: Int?
)

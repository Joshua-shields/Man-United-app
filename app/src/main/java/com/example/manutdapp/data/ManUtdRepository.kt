package com.example.manutdapp.data

import android.util.Log
import android.util.Xml
import com.example.manutdapp.api.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class ManUtdRepository {
    private val api = RetrofitInstance.api
    private val client = OkHttpClient() // Create a client for RSS fetch

    // ... (keep existing date formatters)

    // Helper to format ISO 8601 dates (e.g. 2023-11-24T15:00:00Z) to readable string
    private fun formatDate(isoDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(isoDate)
            val outputFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
            outputFormat.format(date ?: return isoDate)
        } catch (e: Exception) {
            isoDate.substringBefore("T")
        }
    }

    private fun formatTime(isoDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(isoDate)
            val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            outputFormat.format(date ?: return "")
        } catch (e: Exception) {
            ""
        }
    }

    // ... (keep getUpcomingMatches and getRecentResults and getRealTimeStandings)
    
    fun getUpcomingMatches(): Flow<List<Match>> = flow {
        try {
            // Fetch SCHEDULED matches
            val response = api.getManUtdMatches(status = "SCHEDULED")
            val matches = response.matches.take(3).map { apiMatch ->
                Match(
                    id = apiMatch.id,
                    homeTeam = apiMatch.homeTeam.shortName ?: apiMatch.homeTeam.name,
                    awayTeam = apiMatch.awayTeam.shortName ?: apiMatch.awayTeam.name,
                    date = formatDate(apiMatch.utcDate),
                    time = formatTime(apiMatch.utcDate),
                    status = MatchStatus.UPCOMING,
                    competition = apiMatch.competition.name
                )
            }
            emit(matches)
        } catch (e: Exception) {
            Log.e("Repo", "Error fetching upcoming: ${e.message}")
            // Fallback to empty or cache in a real app
            emit(emptyList()) 
        }
    }

    fun getRecentResults(): Flow<List<Match>> = flow {
        try {
            // Fetch FINISHED matches
            val response = api.getManUtdMatches(status = "FINISHED")
            // The API returns them in date order usually, we want most recent first
            val matches = response.matches.reversed().take(5).map { apiMatch ->
                Match(
                    id = apiMatch.id,
                    homeTeam = apiMatch.homeTeam.shortName ?: apiMatch.homeTeam.name,
                    awayTeam = apiMatch.awayTeam.shortName ?: apiMatch.awayTeam.name,
                    date = formatDate(apiMatch.utcDate),
                    time = "FT",
                    status = MatchStatus.FINISHED,
                    homeScore = apiMatch.score.fullTime.home,
                    awayScore = apiMatch.score.fullTime.away,
                    competition = apiMatch.competition.name
                )
            }
            emit(matches)
        } catch (e: Exception) {
            Log.e("Repo", "Error fetching results: ${e.message}")
            emit(emptyList())
        }
    }

    fun getRealTimeStandings(): Flow<List<TeamStanding>> = flow {
        try {
            val response = api.getPLStandings()
            // The response structure has a list of tables (TOTAL, HOME, AWAY). We want "TOTAL".
            val totalTable = response.standings.find { it.type == "TOTAL" }?.table ?: emptyList()
            
            val standings = totalTable.map { pos ->
                TeamStanding(
                    position = pos.position,
                    name = pos.team.shortName ?: pos.team.name,
                    played = pos.playedGames,
                    won = pos.won,
                    drawn = pos.draw,
                    lost = pos.lost,
                    goalDifference = pos.goalDifference,
                    points = pos.points
                )
            }
            emit(standings)
        } catch (e: Exception) {
            Log.e("Repo", "Error fetching standings: ${e.message}")
            emit(emptyList())
        }
    }
    
    fun getSquad(): Flow<List<Player>> = flow {
        try {
            val response = api.getManUtdSquad()
            
            // Map API positions to standard positions and assign shirt numbers
            val shirtNumberMap = mapOf(
                "Bruno Fernandes" to 8,
                "Casemiro" to 18,
                "Harry Maguire" to 5,
                "Lisandro Martínez" to 6,
                "Diogo Dalot" to 20,
                "Luke Shaw" to 23,
                "Matthijs de Ligt" to 4,
                "Noussair Mazraoui" to 3,
                "Tyrell Malacia" to 12,
                "Mason Mount" to 7,
                "Manuel Ugarte" to 25,
                "Kobbie Mainoo" to 37,
                "Amad Diallo" to 16,
                "Joshua Zirkzee" to 11,
                "Tom Heaton" to 22,
                "Altay Bayındır" to 1,
                "Patrick Dorgu" to 19,
                "Leny Yoro" to 15,
                "Benjamin Šeško" to 9,
                "Bryan Mbeumo" to 10,
                "Matheus Cunha" to 14
            )
            
            val players = response.squad.map { apiPlayer ->
                // Standardize positions
                val standardPosition = when {
                    apiPlayer.position.contains("Goalkeeper", ignoreCase = true) -> "Goalkeeper"
                    apiPlayer.position.contains("Back", ignoreCase = true) || 
                    apiPlayer.position.contains("Defence", ignoreCase = true) -> "Defence"
                    apiPlayer.position.contains("Midfield", ignoreCase = true) -> "Midfield"
                    else -> "Offence"
                }
                
                Player(
                    id = apiPlayer.id,
                    name = apiPlayer.name,
                    position = standardPosition,
                    shirtNumber = shirtNumberMap[apiPlayer.name] ?: apiPlayer.shirtNumber,
                    nationality = apiPlayer.nationality,
                    dateOfBirth = apiPlayer.dateOfBirth,
                    // No reliable photo source, will use initials fallback in UI
                    photoUrl = null
                )
            }
            emit(players)
        } catch (e: Exception) {
            Log.e("Repo", "Error fetching squad: ${e.message}", e)
            emit(emptyList())
        }
    }

    fun getNews(): Flow<List<NewsItem>> = flow {
        val newsItems = mutableListOf<NewsItem>()
        try {
             withContext(Dispatchers.IO) {
                // Use The Guardian's Man United RSS feed - most reliable
                val request = Request.Builder()
                    .url("https://www.theguardian.com/football/manchester-united/rss")
                    .addHeader("User-Agent", "Mozilla/5.0")
                    .build()
                
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                
                if (responseBody != null && response.isSuccessful) {
                    parseRssFeed(responseBody, newsItems)
                } else {
                    Log.w("Repo", "Guardian feed failed, trying BBC RSS")
                    // Fallback to BBC if Guardian fails
                    val bbcRequest = Request.Builder()
                        .url("https://feeds.bbci.co.uk/sport/football/teams/manchester-united/rss.xml")
                        .addHeader("User-Agent", "Mozilla/5.0")
                        .build()
                    
                    val bbcResponse = client.newCall(bbcRequest).execute()
                    val bbcBody = bbcResponse.body?.string()
                    
                    if (bbcBody != null) {
                        parseRssFeed(bbcBody, newsItems)
                    }
                }
            }
            
            // If no news items were fetched, provide some fallback mock news
            if (newsItems.isEmpty()) {
                Log.w("Repo", "No news fetched, using fallback")
                emit(getFallbackNews())
            } else {
                emit(newsItems)
            }
        } catch (e: Exception) {
            Log.e("Repo", "Error fetching news RSS: ${e.message}", e)
            // Emit fallback news on error
            emit(getFallbackNews())
        }
    }
    
    private fun parseRssFeed(responseBody: String, newsItems: MutableList<NewsItem>) {
        try {
            val parser = XmlPullParserFactory.newInstance().newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(StringReader(responseBody))
            
            var eventType = parser.eventType
            var currentTitle = ""
            var currentDescription = ""
            var currentLink = ""
            var currentPubDate = ""
            var currentImage: String? = null
            var insideItem = false
            
            var idCounter = 0

            while (eventType != XmlPullParser.END_DOCUMENT) {
                val name = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (name == "item") {
                            insideItem = true
                            currentTitle = ""
                            currentDescription = ""
                            currentLink = ""
                            currentPubDate = ""
                            currentImage = null
                        } else if (insideItem) {
                            when (name) {
                                "title" -> {
                                    if (parser.next() == XmlPullParser.TEXT) {
                                        currentTitle = parser.text
                                    }
                                }
                                "description" -> {
                                    if (parser.next() == XmlPullParser.TEXT) {
                                        currentDescription = parser.text
                                    }
                                }
                                "link" -> {
                                    if (parser.next() == XmlPullParser.TEXT) {
                                        currentLink = parser.text
                                    }
                                }
                                "pubDate" -> {
                                    if (parser.next() == XmlPullParser.TEXT) {
                                        currentPubDate = parser.text
                                    }
                                }
                                "media:thumbnail" -> {
                                    // BBC uses media:thumbnail
                                    currentImage = parser.getAttributeValue(null, "url")
                                }
                                "media:content" -> {
                                    // Guardian uses media:content - get the medium-sized one (460px)
                                    val width = parser.getAttributeValue(null, "width")
                                    if (currentImage == null || width == "460" || width == "700") {
                                        currentImage = parser.getAttributeValue(null, "url")
                                    }
                                }
                                "enclosure" -> {
                                    // Some feeds use enclosure
                                    if (currentImage == null) {
                                        currentImage = parser.getAttributeValue(null, "url")
                                    }
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (name == "item" && insideItem) {
                            // Clean up description (remove HTML tags and truncate)
                            val cleanDesc = currentDescription
                                .replace(Regex("<.*?>"), "")
                                .replace("&lt;", "<")
                                .replace("&gt;", ">")
                                .replace("&amp;", "&")
                                .trim()
                                .take(200) // Limit to 200 chars
                            
                            // Format Date
                            val shortDate = formatRssDate(currentPubDate)
                            
                            // Only add items with titles (skip empty/malformed items)
                            if (currentTitle.isNotEmpty() && !currentTitle.contains("Get in touch", ignoreCase = true)) {
                                newsItems.add(
                                    NewsItem(
                                        id = idCounter++,
                                        title = currentTitle,
                                        summary = cleanDesc.ifEmpty { "Manchester United news update" },
                                        imageUrl = currentImage,
                                        date = shortDate
                                    )
                                )
                            }
                            insideItem = false
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e("Repo", "Error parsing RSS: ${e.message}", e)
        }
    }
    
    private fun formatRssDate(pubDate: String): String {
        return try {
            if (pubDate.isEmpty()) return ""
            // RSS date format: "Wed, 19 Nov 2025 12:00:00 GMT"
            val parts = pubDate.split(" ")
            if (parts.size >= 4) {
                "${parts[2]} ${parts[1]}" // "19 Nov"
            } else {
                pubDate.take(16)
            }
        } catch (e: Exception) {
            pubDate.take(16)
        }
    }
    
    private fun getFallbackNews(): List<NewsItem> {
        return listOf(
            NewsItem(
                id = 1,
                title = "Manchester United Training Update",
                summary = "The squad continues preparations ahead of the upcoming fixture. Check back soon for live updates.",
                imageUrl = null,
                date = "Today"
            ),
            NewsItem(
                id = 2,
                title = "Match Preview: Upcoming Fixture",
                summary = "All you need to know ahead of Manchester United's next match. Team news and analysis.",
                imageUrl = null,
                date = "Today"
            ),
            NewsItem(
                id = 3,
                title = "Club Statement",
                summary = "Stay tuned for the latest official news from Manchester United.",
                imageUrl = null,
                date = "Today"
            )
        )
    }
}


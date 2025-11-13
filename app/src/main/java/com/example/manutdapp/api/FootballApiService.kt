package com.example.manutdapp.api

import com.example.manutdapp.data.ApiMatchesResponse
import com.example.manutdapp.data.ApiStandingsResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

// Replace this with your actual API Key from football-data.org
// You can get one for free at https://www.football-data.org/client/register
const val API_KEY = "a1f6163eeaa0483c9516ea70ed9e825a" 

interface FootballService {
    
    // Fetch matches for Man Utd (Team ID 66)
    @GET("teams/66/matches")
    suspend fun getManUtdMatches(
        @Header("X-Auth-Token") apiKey: String = API_KEY,
        @Query("status") status: String? = null, // "SCHEDULED", "LIVE", "FINISHED"
        @Query("limit") limit: Int = 10
    ): ApiMatchesResponse

    // Fetch PL Standings (Competition ID 2021 for Premier League)
    @GET("competitions/2021/standings")
    suspend fun getPLStandings(
        @Header("X-Auth-Token") apiKey: String = API_KEY
    ): ApiStandingsResponse
    
    // Fetch Man Utd Squad (Team ID 66)
    @GET("teams/66")
    suspend fun getManUtdSquad(
        @Header("X-Auth-Token") apiKey: String = API_KEY
    ): com.example.manutdapp.data.ApiSquadResponse
}

object RetrofitInstance {
    private const val BASE_URL = "https://api.football-data.org/v4/"

    val api: FootballService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FootballService::class.java)
    }
}

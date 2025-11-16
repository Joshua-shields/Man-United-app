package com.example.manutdapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manutdapp.data.ManUtdRepository
import com.example.manutdapp.data.Match
import com.example.manutdapp.data.NewsItem
import com.example.manutdapp.data.Player
import com.example.manutdapp.data.TeamStanding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ManUtdViewModel : ViewModel() {
    private val repository = ManUtdRepository()

    private val _upcomingMatches = MutableStateFlow<List<Match>>(emptyList())
    val upcomingMatches: StateFlow<List<Match>> = _upcomingMatches.asStateFlow()

    private val _recentResults = MutableStateFlow<List<Match>>(emptyList())
    val recentResults: StateFlow<List<Match>> = _recentResults.asStateFlow()

    private val _standings = MutableStateFlow<List<TeamStanding>>(emptyList())
    val standings: StateFlow<List<TeamStanding>> = _standings.asStateFlow()

    private val _news = MutableStateFlow<List<NewsItem>>(emptyList())
    val news: StateFlow<List<NewsItem>> = _news.asStateFlow()
    
    private val _squad = MutableStateFlow<List<Player>>(emptyList())
    val squad: StateFlow<List<Player>> = _squad.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            repository.getUpcomingMatches().collect { _upcomingMatches.value = it }
        }
        viewModelScope.launch {
            repository.getRecentResults().collect { _recentResults.value = it }
        }
        viewModelScope.launch {
            repository.getRealTimeStandings().collect { _standings.value = it }
        }
        viewModelScope.launch {
            repository.getNews().collect { _news.value = it }
        }
        viewModelScope.launch {
            repository.getSquad().collect { _squad.value = it }
        }
    }
}

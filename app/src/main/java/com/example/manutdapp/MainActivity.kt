package com.example.manutdapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.manutdapp.ui.ManUtdViewModel
import com.example.manutdapp.ui.screens.MatchesScreen
import com.example.manutdapp.ui.screens.NewsScreen
import com.example.manutdapp.ui.screens.SquadScreen
import com.example.manutdapp.ui.screens.StandingsScreen
import com.example.manutdapp.ui.theme.ManUtdAppTheme
import com.example.manutdapp.ui.theme.UtdRed
import com.example.manutdapp.ui.theme.UtdGold

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val viewModel: ManUtdViewModel by viewModels()
        
        setContent {
            // Always use dark theme
            ManUtdAppTheme(darkTheme = true) {
                MainScreen(viewModel)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Matches : Screen("matches", "Matches", Icons.Filled.SportsSoccer)
    object Squad : Screen("squad", "Squad", Icons.Filled.Group)
    object Standings : Screen("standings", "Standings", Icons.Filled.EmojiEvents)
    object News : Screen("news", "News", Icons.Filled.Article)
}

@Composable
fun MainScreen(viewModel: ManUtdViewModel) {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Matches,
        Screen.Squad,
        Screen.Standings,
        Screen.News
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = UtdRed
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = UtdRed,
                            selectedTextColor = UtdRed,
                            indicatorColor = UtdGold.copy(alpha = 0.2f),
                            unselectedIconColor =  MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Matches.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Matches.route) {
                MatchesScreen(viewModel)
            }
            composable(Screen.Squad.route) {
                SquadScreen(viewModel)
            }
            composable(Screen.Standings.route) {
                StandingsScreen(viewModel)
            }
            composable(Screen.News.route) {
                NewsScreen(viewModel)
            }
        }
    }
}

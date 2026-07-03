package com.example.finals_comic_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.finals_comic_app.data.local.AppDatabase
import com.example.finals_comic_app.data.repository.MangaRepository
import com.example.finals_comic_app.ui.navigation.Screen
import com.example.finals_comic_app.ui.screens.DetailsScreen
import com.example.finals_comic_app.ui.screens.HomeScreen
import com.example.finals_comic_app.ui.screens.SearchScreen
import com.example.finals_comic_app.ui.screens.FavoritesScreen
import com.example.finals_comic_app.ui.screens.NotificationsScreen
import com.example.finals_comic_app.ui.screens.SettingsScreen
import com.example.finals_comic_app.ui.theme.Finals_comic_appTheme
import com.example.finals_comic_app.ui.viewmodel.MangaViewModel
import com.example.finals_comic_app.ui.viewmodel.MangaViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Finals_comic_appTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { MangaRepository(database.mangaDao()) }
    val viewModel: MangaViewModel = viewModel(
        factory = MangaViewModelFactory(repository)
    )
    val navController = rememberNavController()
    
    val items = listOf(
        Triple(Screen.Home, Icons.Default.Home, "Home"),
        Triple(Screen.MyList, Icons.AutoMirrored.Filled.MenuBook, "My List"),
        Triple(Screen.Search, Icons.Default.Search, "Search"),
        Triple(Screen.Profile, Icons.Default.Person, "Profile")
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { (screen, icon, label) ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                icon, 
                                contentDescription = label,
                                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            ) 
                        },
                        label = { 
                            Text(
                                label, 
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            ) 
                        },
                        selected = selected,
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
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(viewModel = viewModel, onMangaClick = { malId ->
                    navController.navigate(Screen.Details.createRoute(malId))
                })
            }
            composable(Screen.Search.route) {
                SearchScreen(viewModel = viewModel, onMangaClick = { malId ->
                    navController.navigate(Screen.Details.createRoute(malId))
                })
            }
            composable(Screen.MyList.route) {
                FavoritesScreen(viewModel = viewModel, onMangaClick = { malId ->
                    navController.navigate(Screen.Details.createRoute(malId))
                })
            }
            composable(Screen.Profile.route) { SettingsScreen() }
            composable(
                route = Screen.Details.route,
                arguments = listOf(navArgument("mangaId") { type = NavType.IntType })
            ) { backStackEntry ->
                val mangaId = backStackEntry.arguments?.getInt("mangaId") ?: 0
                LaunchedEffect(mangaId) {
                    viewModel.selectMangaById(mangaId)
                }
                DetailsScreen(viewModel = viewModel, onBack = {
                    navController.popBackStack()
                })
            }
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text(text = "$name Screen", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

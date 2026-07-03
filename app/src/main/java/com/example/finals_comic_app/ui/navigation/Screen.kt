package com.example.finals_comic_app.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object MyList : Screen("favorites")
    object Categories : Screen("categories")
    object Search : Screen("search_screen")
    object Profile : Screen("profile")
    object Details : Screen("details/{mangaId}") {
        fun createRoute(mangaId: Int) = "details/$mangaId"
    }
}

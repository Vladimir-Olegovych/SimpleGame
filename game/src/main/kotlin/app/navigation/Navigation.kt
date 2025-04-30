package app.navigation

sealed class Navigation {
    data class Main(val label: String): Navigation()
    data object Game : Navigation()
}
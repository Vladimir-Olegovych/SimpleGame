package app.navigation

sealed class Navigation {
    data object Main: Navigation()
    data object Game : Navigation()
    data object StructureEditor : Navigation()
}
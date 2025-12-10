package app.event

sealed class UiEvent {
    class Resize(val width: Int, val height: Int)
}
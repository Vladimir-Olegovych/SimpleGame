package app.events

sealed class GameEvent {
    class UpdateInventory(val entityId: Int): GameEvent()
    class OpenInventory(val entityId: Int): GameEvent()
    class HideInventory: GameEvent()
}
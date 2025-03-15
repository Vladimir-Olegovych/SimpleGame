package types

enum class EventType(val id: Int) {
    DISCONNECTED(0),
    CONNECTED(1),
    ERROR(2),
    BODY_PHYSICAL(3),
}
package models.entity

enum class EntityType(val order: Int) {
    NULL(0),
    BACKGROUND(1),
    STRUCTURE(2),
    ENTITY(2),
    EFFECTS(3),
    CEILING(4)
}
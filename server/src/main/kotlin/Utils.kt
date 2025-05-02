package org.example

import org.example.ecs.components.Entity

fun canSend(
    playerEntity: Entity,
    otherEntity: Entity
): Boolean {
    val playerBody = playerEntity.body?: return false
    val otherBody = otherEntity.body?: return false
    val playerPosition = playerBody.position
    val otherPosition = otherBody.position
    if ((otherPosition.x - playerPosition.x) !in -100F..100F) {
        otherBody.isActive = false
        return false
    }
    if ((otherPosition.y - playerPosition.y) !in -100F..100F) {
        otherBody.isActive = false
        return false
    }
    otherBody.isActive = true
    return true
}
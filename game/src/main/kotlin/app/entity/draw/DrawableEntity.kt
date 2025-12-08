package app.entity.draw

import models.entity.EntityType

data class DrawableEntity(
    val entityId: Int,
    val yPosition: Float,
    val entityType: EntityType
) : Comparable<DrawableEntity> {
    override fun compareTo(other: DrawableEntity): Int {
        val layerCompare = this.entityType.order.compareTo(other.entityType.order)
        if (layerCompare != 0) return layerCompare

        // Внутри одного слоя ENTITIES сортируем по Y (чем выше Y, тем раньше рисуем)
            /*
        if (this.entityType == EntityType.ENTITY) {
            return other.yPosition.compareTo(this.yPosition) // обратный порядок
        }


        return this.entityId.compareTo(other.entityId)

             */
        return other.yPosition.compareTo(this.yPosition)
    }
}
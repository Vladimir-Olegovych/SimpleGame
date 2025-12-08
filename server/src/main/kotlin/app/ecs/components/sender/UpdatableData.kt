package app.ecs.components.sender

interface UpdatableData<U> {
    fun hasUpdate(): Boolean
    fun markAsUpdated()
    fun getUpdate(): U
    fun getAll(): U
}
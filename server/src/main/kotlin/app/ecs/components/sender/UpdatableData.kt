package app.ecs.components.sender

abstract class UpdatableData<U> {

    var marked = false
    var wasUpdated = false

    fun hasUpdate(): Boolean {
        if (wasUpdated) return true
        val hasUpdate = onHasUpdate()
        if (hasUpdate) wasUpdated = true
        return hasUpdate
    }

    fun markAsUpdated() {
        if (marked) return
        onMarkAsUpdated()
        marked = true
    }

    fun finishUpdate(){
        marked = false
        wasUpdated = false
        onMarkAsUpdated()
    }

    protected abstract fun onHasUpdate(): Boolean
    protected abstract fun onMarkAsUpdated()
    abstract fun getUpdate(): U
    abstract fun getAll(): U
}
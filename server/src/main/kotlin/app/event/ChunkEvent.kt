package app.event

import alexey.tools.common.collections.IntCollection

sealed class ChunkEvent {
    class Show(val entities: IntCollection, val activators: IntCollection)
    class Hide(val entities: IntCollection, val activators: IntCollection)
}
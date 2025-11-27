package org.example.core.models.server

import event.Event

class EventContainer<T: Event>(val entityId: Int, val event: T)
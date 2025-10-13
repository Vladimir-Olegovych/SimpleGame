package org.example.core

inline fun <reified T> Iterable<*>.getType(): T {
    for (element in this) {
        if (element is T) {
            return element
        }
    }
    throw NullPointerException("${T::class.java} not found in ${this::class.java}")
}

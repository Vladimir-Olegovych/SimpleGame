package org.example.core

import tools.chunk.ChunkManager
import tools.math.ImmutableIntVector2

fun printChunksGrid(chunks: Map<ImmutableIntVector2, ChunkManager.MutableChunk>) {
    if (chunks.isEmpty()) return

    val xs = chunks.keys.map { it.x }
    val ys = chunks.keys.map { it.y }

    val minX = xs.minOrNull() ?: 0
    val maxX = xs.maxOrNull() ?: 0
    val minY = ys.minOrNull() ?: 0
    val maxY = ys.maxOrNull() ?: 0

    val width = maxX - minX + 1
    val height = maxY - minY + 1

    val grid = Array(height) { CharArray(width) { ' ' } }

    for ((pos, chunk) in chunks) {
        val row = pos.y - minY
        val col = pos.x - minX
        if (row in 0 until height && col in 0 until width) {
            grid[row][col] = when(1){
                1 -> if (chunk.observers.isNotEmpty()) '❏' else '▫'
                else -> '-'
            }
        }
    }

    for (row in grid) {
        println(row.joinToString(separator = " "))
    }
}

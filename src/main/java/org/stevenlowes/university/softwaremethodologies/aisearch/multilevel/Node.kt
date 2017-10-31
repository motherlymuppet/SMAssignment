package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel

abstract class Node(val id: Int, val level: Level) {
    fun distanceTo(other: Node) {
        assert(level == other.level)
        level.distanceBetween(this, other)
    }

    abstract fun solve(entryNode: Node): List<Node>
}
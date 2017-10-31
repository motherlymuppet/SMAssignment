package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel

abstract class Node(val id: Int, val level: Level) {
    fun distanceTo(other: Node) {
        assert(level == other.level)
        level.distanceBetween(this, other)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (id != other.id) return false
        if (level != other.level) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + level.hashCode()
        return result
    }

    override fun toString(): String {
        return "Node(id=$id)"
    }
}
package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes

import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Level

abstract class Node(val id: Int, val level: Level, val childNodes: Collection<Node>) {

    abstract val entryNode: Node

    abstract val exitNode: Node

    fun distanceTo(other: Node): Float {
        assert(level == other.level)
        return level.distanceBetween(this, other)
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
        return "Node(id=$id level=${level.id})"
    }
}
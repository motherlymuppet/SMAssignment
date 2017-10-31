package org.stevenlowes.university.softwaremethodologies.aisearch.algorithms.grouping.GroupBuilder

class Node(val id: Int) {
    val connections: MutableMap<Node, Float> = mutableMapOf()
    var inverseResistance: Float = 0f
    val resistance: Float get() = if (inverseResistance == 0f) 0f else 1 / inverseResistance
    val connectivity = 1 / connections.values.map { 1 / it }.sum()

    override fun toString(): String {
        return "Node(id=$id)"
    }
}
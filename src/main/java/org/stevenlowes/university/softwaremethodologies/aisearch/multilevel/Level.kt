package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel

import org.stevenlowes.university.softwaremethodologies.aisearch.DistanceArray
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node

class Level(val id: Int) {

    lateinit var nodes: Set<Node> private set
    lateinit var array: DistanceArray private set

    fun setNodes(newDistances: Map<Node, Map<Node, Float>>) {
        val distances = newDistances.mapKeys { it.key.id }.mapValues { it.value.mapKeys { it.key.id} }
        array = DistanceArray(distances)
        nodes = newDistances.keys
    }

    fun distanceBetween(node1: Node, node2: Node): Float {
        return array.get(node1.id, node2.id)
    }
}
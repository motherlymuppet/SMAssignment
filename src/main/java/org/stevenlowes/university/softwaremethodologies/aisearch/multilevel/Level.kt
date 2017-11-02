package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel

import org.stevenlowes.university.softwaremethodologies.aisearch.FastSquareArray
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node

class Level(val id: Int) {

    lateinit var nodes: Set<Node> private set
    lateinit var array: FastSquareArray private set

    fun setNodes(newDistances: Map<Node, Map<Node, Float>>) {
        array = FastSquareArray(newDistances.size, newDistances)
        nodes = newDistances.keys
    }

    fun distanceBetween(node1: Node, node2: Node): Float {
        return array.get(node1.id, node2.id)
    }
}
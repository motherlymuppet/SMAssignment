package org.stevenlowes.university.softwaremethodologies.aisearch.input

import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node

data class Path(val nodes: List<Node>) {
    val distance: Float by lazy { calcDistance() }

    private fun calcDistance(): Float{
        var prevNode: Node? = null
        var distance: Float = 0f

        for(node in nodes){
            if(prevNode != null){
                val dist = prevNode.distanceTo(node)
                distance += dist
            }
            prevNode = node
        }

        return distance
    }
}
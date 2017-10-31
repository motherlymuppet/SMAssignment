package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel

class Level(val nodes: Set<Node>) {

    val distances: MutableMap<Node, MutableMap<Node, Float>> = mutableMapOf()

    fun addCities(newDistances: MutableMap<Node, MutableMap<Node, Float>>) {
        distances.putAll(newDistances)
    }

    fun distanceBetween(node1: Node, node2: Node): Float {
        assert(node1 in nodes)
        assert(node2 in nodes)

        val direction1 = directionalDistance(node1, node2)
        if (direction1 == null) {
            val direction2 = directionalDistance(node2, node1)!!
            return direction2
        }
        else {
            return direction1
        }
    }

    private fun directionalDistance(node1: Node, node2: Node): Float? {
        return distances.get(node1)?.get(node2)
    }
}
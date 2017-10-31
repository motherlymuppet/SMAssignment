package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel

class Level() {

    val nodes: MutableSet<Node> = mutableSetOf()
    lateinit var distances: Map<Node, Map<Node, Float>> private set

    fun addNodes(newDistances: Map<Node, Map<Node, Float>>) {
        distances = newDistances
        nodes.addAll(newDistances.keys)
    }

    fun distanceBetween(node1: Node, node2: Node): Float {
        assert(node1 in nodes)
        assert(node2 in nodes)

        val direction1 = directionalDistance(node1, node2)
        if (direction1 == null) {
            try {
                val direction2 = directionalDistance(node2, node1)!!
                return direction2
            }
            catch (ex: Exception) {
                println("here")
                return 0f
            }
        }
        else {
            return direction1
        }
    }

    private fun directionalDistance(node1: Node, node2: Node): Float? {
        return distances.get(node1)?.get(node2)
    }
}
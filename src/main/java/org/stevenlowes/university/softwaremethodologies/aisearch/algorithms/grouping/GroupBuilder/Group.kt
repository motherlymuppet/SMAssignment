package org.stevenlowes.university.softwaremethodologies.aisearch.algorithms.grouping.GroupBuilder

class Group private constructor(var energy: Float) {
    val possibleNext: MutableSet<Node> = mutableSetOf()
    val chosen: MutableSet<Node> = mutableSetOf()

    fun addNode(node: Node) {
        energy -= node.resistance
        possibleNext.remove(node)
        chosen.add(node)
        possibleNext.addAll(node.connections.keys.minus(chosen))

        node.connections.keys.forEach {
            //Adjust the inverse resistance to account for the fact we have another connection now
            val branchResistance = node.resistance + node.connections[it]!!
            it.inverseResistance += 1 / branchResistance
        }
    }

    companion object {
        fun createGroup(energy: Float, startNode: Node): Set<Node> {
            val group = Group(energy)

            while (true) {
                val mostConnected = group.possibleNext.maxBy { it.inverseResistance }
                val next = if (mostConnected == null) {
                    if (group.chosen.isEmpty()) {
                        startNode
                    }
                    else {
                        break
                    }
                }
                else {
                    mostConnected
                }
                val nextResistance = next.resistance
                if (group.energy < nextResistance) {
                    break
                }
                else {
                    group.addNode(next)
                    println("Added node ${next.id} to group. Energy cost: $nextResistance Energy remaining: ${group.energy}")
                }
            }
            return group.chosen
        }
    }
}
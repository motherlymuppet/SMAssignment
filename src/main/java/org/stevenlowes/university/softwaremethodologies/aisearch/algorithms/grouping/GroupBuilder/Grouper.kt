package org.stevenlowes.university.softwaremethodologies.aisearch.algorithms.grouping.GroupBuilder

import org.stevenlowes.university.softwaremethodologies.aisearch.input.Matrix
import org.stevenlowes.university.softwaremethodologies.aisearch.input.TextParser

class Grouper(matrix: Matrix) {
    val nodes: Collection<Node> get() = nodesByIndex.values
    val nodesByIndex: Map<Int, Node>

    init {
        nodesByIndex = matrix.cities.map { id ->
            id to Node(id)
        }.toMap()

        matrix.distances.forEach { cities, distance ->
            val distanceFloat = distance.toFloat()
            val city1 = nodesByIndex[cities.first]!!
            val city2 = nodesByIndex[cities.second]!!
            assert(city1 != city2)
            city1.connections.put(city2, distanceFloat)
            city2.connections.put(city1, distanceFloat)
        }
    }

    fun build(energy: Float): Set<Set<Node>> {
        println("Starting group build")
        val ungroupedNodes = nodes.toMutableSet()
        val groups = mutableSetOf<Set<Node>>()
        while (ungroupedNodes.isNotEmpty()) {
            println("Building group")
            val root = ungroupedNodes.maxBy { it.connectivity }!!
            val group = Group.createGroup(energy, root)
            groups.add(group)
            ungroupedNodes.removeAll(group)

            nodes.forEach { node ->
                group.forEach { used ->
                    node.connections.remove(used)
                }
            }

            nodes.forEach { node ->
                node.inverseResistance = 0f
            }

            println("Finished building group. Moving onto next group")
        }
        return groups
    }
}

fun main(args: Array<String>) {
    println("Starting to load file")
    val matrix = TextParser.parseFile("testFiles/10.txt")
    println("Finished loading file")
    println("Starting creating grouper")
    val grouper = Grouper(matrix)
    println("Finished creating grouper")
    println("Starting building groups")
    val nodes = grouper.build(500f)
    println("Finished building groups")
    nodes.forEach { println(it) }
}
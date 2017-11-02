package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.groupers

import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Level
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.SimpleGroupNode

class EnergeticGrouper(val energy: Float, val minGroupSize: Int) : Grouper {
    override fun group(level: Level): Level {
        val nodes = level.nodes
        val distAdjFactor = (1 / level.array.average)
        val grouped: MutableSet<Node> = mutableSetOf()
        val ungrouped: MutableSet<Node> = nodes.toMutableSet()
        val groups: MutableSet<Set<Node>> = mutableSetOf()
        while (ungrouped.isNotEmpty()) {
            val group = groupOnce(ungrouped, level, distAdjFactor)
            groups.add(group)
            grouped.addAll(group)
            ungrouped.removeAll(group)
        }
        val parent = Level(level.id + 1)
        val newNodes = groups.withIndex().map { SimpleGroupNode(it.index, parent, it.value) }
        val distances = distanceMatrix(newNodes, level)
        parent.setNodes(distances)

        return parent
    }

    private fun distanceMatrix(nodes: Collection<Node>, level: Level): Map<Node, Map<Node, Float>> {
        val matrix = nodes.map { n1 ->
            n1 to nodes.mapNotNull { n2 ->
                //For each pair of nodes return
                if (n1 == n2) {
                    null
                }
                else {
                    n2 to distanceBetween(n1, n2, level)
                }
            }.toMap()
        }.toMap()

        val adjustment = 1 / (matrix.values.flatMap { it.values }.average())

        val adjusted = matrix.mapValues { it.value.mapValues { (it.value * adjustment).toFloat() } }

        return adjusted
    }

    private fun distanceBetween(n1: Node, n2: Node, level: Level): Float {
        var inverseResistance: Double = 0.0

        n1.childNodes.forEach { n1Child ->
            n2.childNodes.forEach { n2Child ->
                inverseResistance += level.distanceBetween(n1Child, n2Child)
            }
        }

        return (1 / inverseResistance).toFloat();
    }

    private fun groupOnce(nodes: Collection<Node>, level: Level, distAdjFactor: Float): Set<Node> {
        println("Starting to group. ${nodes.size} nodes left")
        val selected = mutableSetOf<TempNode>()
        val notSelected = nodes.map { TempNode(it) }.toMutableSet()
        val startTempNode = notSelected.first()
        var remainingEnergy = energy
        var first = true

        while (notSelected.isNotEmpty()) { //End if we select all nodes too
            val toAdd = if (first) {
                first = false
                startTempNode
            }
            else {
                notSelected.maxBy { it.inverseResistance }!!
            }

            if (remainingEnergy < toAdd.resistance && selected.size >= minGroupSize) {
                break
            }
            else {
                remainingEnergy -= toAdd.resistance
                selected.add(toAdd)
                notSelected.remove(toAdd)
                notSelected.forEach { oth ->
                    oth.inverseResistance = oth.inverseResistance +
                            1 / (toAdd.resistance + adjustedDistanceBetween(toAdd, oth, level, distAdjFactor))
                }
            }
        }

        println("Finished grouping that group. $remainingEnergy left and ${selected.size} nodes in the group")
        return selected.map { it.underlying }.toSet()
    }

    private fun adjustedDistanceBetween(toAdd: TempNode, oth: TempNode, level: Level, adjustmentFactor: Float): Float {
        val orig = level.distanceBetween(toAdd.underlying, oth.underlying)
        val adjusted = orig * adjustmentFactor
        return adjusted
    }

    private fun firstNode(alreadyGrouped: Set<Node>,
                          notYetGrouped: Set<Node>,
                          level: Level): Node {
        if (alreadyGrouped.isEmpty()) {
            return notYetGrouped.first()
        }

        return notYetGrouped.maxBy { TempNode(it).connectivity(level) }!!
    }
}
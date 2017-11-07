package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers

import org.stevenlowes.university.softwaremethodologies.aisearch.FastSquareArray
import org.stevenlowes.university.softwaremethodologies.aisearch.NodeArray
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node
import java.util.*

class AntColonySolver() : Solver {
    override fun bestPath(nodes: Collection<Node>): List<Node> {
        while (!terminate()) {
            val ants = generateAnts()
            updatePheremones(ants)
        }
    }

    private fun updatePheremones(ants: List<Ant>) {
        TODO()
    }

    private fun terminate(): Boolean {
        TODO()
    }

    private fun generateAnts(): List<Ant> {
        TODO()
    }
}

private class Ant(val startNode: Int, val desirability: DesirabilityArray) {
    val path: IntArray

    init {
        val chosen = mutableListOf<Int>()
        val size = distances.size
        var chosenCount = 0
        path = IntArray(size)

        while (chosenCount < size) {

            chosenCount++
        }
    }
}

private class PheremonesArray(size: Int, initial: Float) : FastSquareArray(size, { _, _ -> initial }) {
    companion object {
        val rand = Random()
    }

    fun evaporate(evaporationRate: Float) {
        transform { _, _, current ->
            current * evaporationRate
        }
    }

    fun depositAll(increase: Float) {
        transform { _, _, current ->
            Math.max(0f, current + increase)
        }
    }

    fun deposit(ant: Ant, expectedMaxDistance: Float) {
        val path = ant.path
        val distance = ant.distance

        var previous: Int? = null
        for (node in path) {
            if (previous != null) {
                add(previous, node, distance / expectedMaxDistance)
            }
            previous = node
        }
    }
}

private class DesirabilityArray(val distances: NodeArray,
                                val pheremones: PheremonesArray,
                                distanceInfluence: Double,
                                pheremonesInfluence: Double)
    : FastSquareArray(distances.size,
                      { x, y ->
                          (
                                  Math.pow(
                                          pheremones.get(x, y).toDouble(),
                                          pheremonesInfluence) *
                                          Math.pow(
                                                  1 / distances.get(x, y).toDouble(),
                                                  distanceInfluence)
                                  ).toFloat()
                      }
                     ) {
    companion object {
        val rand = Random()
    }

    val runningTotals = FastSquareArray(size, { x, y ->
        (0..y).map { get(x, it) }.sum()
    })

    /**
     * Returns the node that the ant should move to
     */
    fun moveFrom(x: Int): Int {
        val max = get(x, size - 1)
        val random = rand.nextFloat() * max
        (0..(size - 1)).forEach { y ->
            val runningTotalValue = runningTotals.get(x, y)
            if (runningTotalValue >= random) {
                return y
            }
        }
        throw RuntimeException("This should never happen")
    }
}
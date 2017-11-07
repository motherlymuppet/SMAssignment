package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers

import org.stevenlowes.university.softwaremethodologies.aisearch.DistanceArray
import org.stevenlowes.university.softwaremethodologies.aisearch.FastSquareArray
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node
import java.util.*

class AntColonySolver(val antCount: Int,
                      val distanceInfluence: Double,
                      val pheremonesInfluence: Double,
                      val pheremoneEvaporation: Float,
                      val pheremoneDepositing: Float) : Solver {
    override fun bestPath(nodes: Collection<Node>): List<Node> {
        val distances = nodes.first().level.array
        val pheremones = Pheremones(distances, 0.2f)

        var bestAnt: Ant? = null
        while (!terminate()) {
            println("Iterating!")
            val desirability = DesirabilityArray(distances, pheremones, distanceInfluence, pheremonesInfluence)
            val ants = generateAnts(antCount, desirability, distances)
            updatePheremones(ants, pheremones, pheremoneEvaporation, pheremoneDepositing)

            val newBestAnt = ants.minBy { it.distance }
            if (newBestAnt != null) {
                if (bestAnt == null || newBestAnt.distance < bestAnt.distance) {
                    bestAnt = newBestAnt
                    println(bestAnt)
                }
            }
        }

        return bestAnt!!.path.map { nodeId -> nodes.first { node -> node.id == nodeId } }
    }

    companion object {
        val rand = Random()
    }

    private fun updatePheremones(ants: List<Ant>,
                                 pheremones: Pheremones,
                                 pheremoneEvaporation: Float,
                                 pheremoneDepositing: Float) {
        pheremones.evaporate(pheremoneEvaporation)
        pheremones.depositAll(ants, pheremoneDepositing)
    }

    private var count = 0;

    private fun terminate(): Boolean {
        count++
        return count > 10000
    }

    private fun generateAnts(count: Int, desirability: DesirabilityArray, distances: DistanceArray): List<Ant> {
        return (1..count).map {
            val startNode = rand.nextInt(distances.size)
            return@map Ant(startNode, desirability, distances)
        }
    }
}

private class Ant(startNode: Int, desirability: DesirabilityArray, distances: DistanceArray) {
    val path: IntArray
    val distance: Float

    init {
        val chosen = mutableListOf<Int>()
        val size = desirability.size
        path = IntArray(size)

        var previous = startNode
        path[0] = startNode
        chosen.add(startNode)
        var chosenCount = 1

        while (chosenCount < size) {
            val newNode = desirability.moveFrom(previous, chosen)
            chosen.add(newNode)
            path[chosenCount] = newNode
            chosenCount++
            previous = newNode
        }

        distance = distances.getDistance(path)
    }

    override fun toString(): String {
        return "Ant(path=${Arrays.toString(path)}, distance=$distance)"
    }


}

private class Pheremones(val distances: DistanceArray, initial: Float) : FastSquareArray(distances.size,
                                                                                         { _, _ -> initial }) {
    fun evaporate(evaporationRate: Float) {
        val multiplier = (1f - evaporationRate)
        transform { _, _, current ->
            current * multiplier
        }
    }

    fun depositAll(increase: Float) {
        transform { _, _, current ->
            Math.max(0f, current + increase)
        }
    }

    fun depositAll(ants: List<Ant>, pheremoneDepositing: Float) {
        ants.forEach { ant ->
            deposit(ant, pheremoneDepositing)
        }
    }

    fun deposit(ant: Ant,
                pheremoneDepositing: Float) {
        val path = ant.path
        val pairs = (0..(size - 2)).map { path[it] to path[it + 1] }
        pairs.forEach { (x, y) ->
            val distance = distances.get(x, y)
            val amount = pheremoneDepositing / distance
            add(x, y, amount)
        }
    }
}

private class DesirabilityArray(val distances: DistanceArray,
                                val pheremones: Pheremones,
                                distanceInfluence: Double,
                                pheremonesInfluence: Double)
    : FastSquareArray(distances.size,
                      { x, y ->
                          (
                                  Math.pow(pheremones.get(x, y).toDouble(), pheremonesInfluence) *
                                          Math.pow(1 / distances.get(x, y).toDouble(), distanceInfluence)
                                  ).toFloat()
                      }
                     ) {
    companion object {
        val rand = Random()
    }

    /**
     * Returns the node that the ant should move to
     */
    fun moveFrom(x: Int, chosen: MutableList<Int>): Int {
        val max = (0..(size - 1)).filterNot { it in chosen }.map { y -> get(x, y) }.sum()

        val random = rand.nextFloat() * max
        var runningTotal = 0f
        (0..(size - 1)).filterNot { it in chosen }.forEach { y ->
            val value = get(x, y)
            runningTotal += value
            if (runningTotal >= random) {
                return y
            }
        }
        throw RuntimeException("This should never happen")
    }
}
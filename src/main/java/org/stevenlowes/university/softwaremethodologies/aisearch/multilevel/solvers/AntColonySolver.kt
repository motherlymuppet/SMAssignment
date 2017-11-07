package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers

import org.stevenlowes.university.softwaremethodologies.aisearch.DistanceArray
import org.stevenlowes.university.softwaremethodologies.aisearch.FastSquareArray
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class AntColonySolver(val antCount: Int,
                      val distanceInfluence: Double,
                      val pheremonesInfluence: Double,
                      val pheremoneEvaporation: Float,
                      val pheremoneDepositing: Float) : Solver {
    override fun bestPath(nodes: Collection<Node>): List<Node> {
        val distances = nodes.first().level.array
        val pheremones = Pheremones(distances, 0.2f)

        var energy = 10

        var bestAnt: Ant? = null
        while (energy > 0) {
            println("Iterating! $energy")
            val desirability = DesirabilityArray(distances, pheremones, distanceInfluence, pheremonesInfluence)
            val ants = generateAnts(antCount, desirability, distances)
            val depositingAnts = if (bestAnt == null) ants else ants.plus(bestAnt)
            updatePheremones(depositingAnts, pheremones, pheremoneEvaporation, pheremoneDepositing)

            val newBestAnt = ants.minBy { it.distance }!!

            energy -= 1

            if (bestAnt == null || newBestAnt.distance < bestAnt.distance) {
                energy = 10
                bestAnt = newBestAnt
                println("Best Ant: ${bestAnt.distance}")
            }
        }

        return bestAnt!!.path.map { nodeId -> nodes.first { node -> node.id == nodeId } }
    }

    companion object {
        val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    }

    private fun updatePheremones(ants: List<Ant>,
                                 pheremones: Pheremones,
                                 pheremoneEvaporation: Float,
                                 pheremoneDepositing: Float) {
        pheremones.evaporate(pheremoneEvaporation)
        pheremones.depositAll(ants, pheremoneDepositing)
    }

    private fun generateAnts(count: Int, desirability: DesirabilityArray, distances: DistanceArray): List<Ant> {
        if (count * desirability.size * desirability.size > 100 * 1000 * 1000) {
            val cpus = Runtime.getRuntime().availableProcessors()
            val countPer = count / cpus
            val futures = (1..cpus).map { executor.submit(AntGenerator(countPer, desirability, distances)) }
            val ants = futures.flatMap { it.get() }
            return ants
        }
        else {
            //Single threaded
            return AntGenerator(count, desirability, distances).call()
        }
    }
}

private class AntGenerator(val ants: Int,
                           val desirability: DesirabilityArray,
                           val distances: DistanceArray) : Callable<List<Ant>> {
    companion object {
        val rand = Random()
    }

    override fun call(): List<Ant> {
        val size = desirability.size
        return (1..ants).map { Ant(rand.nextInt(size), desirability, distances) }
    }
}

private class Ant(startNode: Int, desirability: DesirabilityArray, distances: DistanceArray) {
    val path: IntArray
    val distance: Float

    init {
        val options = (0..(distances.size - 1)).toMutableList()
        path = IntArray(distances.size)

        var previous = startNode
        path[0] = startNode
        options.remove(startNode)

        var currentIndex = 1
        while (options.isNotEmpty()) {
            val newNode = desirability.moveFrom(previous, options)

            options.remove(newNode)

            path[currentIndex] = newNode
            currentIndex++
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
    fun moveFrom(x: Int, options: List<Int>): Int {
        val random = rand.nextFloat() * getMax(x, options)
        return weightedRandom(x, options, random)
    }

    fun weightedRandom(x: Int, options: List<Int>, random: Float): Int {
        var runningTotal = 0f
        for (y in options) {
            val value = get(x, y)
            runningTotal += value
            if (runningTotal >= random) {
                return y
            }
        }
        throw RuntimeException("This should never happen")
    }

    fun getMax(x: Int, options: List<Int>): Float {
        return options.map { get(x, it) }.sum()
    }
}
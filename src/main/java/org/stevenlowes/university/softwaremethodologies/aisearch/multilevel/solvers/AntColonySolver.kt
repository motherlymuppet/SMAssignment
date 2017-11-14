package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers

import org.stevenlowes.university.softwaremethodologies.aisearch.DistanceArray
import org.stevenlowes.university.softwaremethodologies.aisearch.FastTriangularArray
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class AntColonySolver(val antCount: Int,
                      val distanceInfluence: Double,
                      val pherInfluence: Double,
                      val pherStart: Float,
                      val pherEvaporation: Float,
                      val pherDepositing: Float,
                      val cullingCutoff: Float) : Solver {
    override fun bestPath(nodes: Collection<Node>): List<Node> {
        val distances = nodes.first().level.array
        val pheremones = Pheremones(distances, pherStart)
        val culling = CullingArray(distances.size)

        var energy = 10

        var bestAnt: Ant? = null
        while (energy > 0) {
            println()
            println("Iterating! $energy")
            val total = culling.array.size
            val culled = culling.array.count { it == 1f }
            val percent = (culled.toDouble()) / (total.toDouble())
            println("Culled $culled of $total ($percent%)")

            val desirability = DesirabilityArray(distances, pheremones, culling, distanceInfluence, pherInfluence)
            desirability.compact()
            desirability.normalise()

            val ants = generateAnts(antCount, desirability, distances)
            val depositingAnts = if (bestAnt == null) ants else ants.plus(bestAnt)

            updatePheremones(depositingAnts, pheremones, pherEvaporation, pherDepositing)

            pheremones.transform { x, y, value ->
                if (value < cullingCutoff) {
                    culling.cull(x, y)
                }
                value
            }

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
            println("Generating ants using $cpus threads")
            val countPer = count / cpus
            val futures = (1..cpus).map { executor.submit(AntGenerator(countPer, desirability, distances)) }
            val ants = futures.flatMap { it.get() }
            return ants
        }
        else {
            //Single threaded
            println("Generating ants using single-thread")
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

            val removed = options.remove(newNode)
            if (!removed) {
                println("here")
            }

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

private class Pheremones(val distances: DistanceArray, initial: Float) : FastTriangularArray(distances.size,
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
            if (distance == 0f) {
                add(x, y, 1f)
            }
            else {
                val amount = pheremoneDepositing / distance
                add(x, y, amount)
            }
        }
    }
}

private class DesirabilityArray(val distances: DistanceArray,
                                val pheremones: Pheremones,
                                val culling: CullingArray,
                                distanceInfluence: Double,
                                pheremonesInfluence: Double)
    : FastTriangularArray(distances.size,
                          { x, y ->
                          val culled = culling.isCulled(x, y)
                          if (culled) {
                              -1f
                          }
                          else {
                              val pheremone = pheremones.get(x, y).toDouble()
                              val distance = distances.get(x, y).toDouble()
                              val pheremonesPow = Math.pow(pheremone, pheremonesInfluence)
                              val invDist = 1 / distance
                              val invDistPow = Math.pow(invDist, distanceInfluence)
                              (pheremonesPow * invDistPow).toFloat()
                          }
                      }
                         ) {

    private val idArray = FastTriangularArray(distances.size, { x, _ -> x.toFloat() })

    fun compact() {
        println()
        println("Before:")
        idArray.print()
        (0..(size - 1)).forEach { x ->
            var swapPoint = size - 1
            var y = 0
            while (y < swapPoint) {
                val desirability = get(x, y)
                if (desirability == -1f) {
                    swap(x, y, swapPoint)
                    println("Swapping $x, $y")
                    swapPoint--
                }
                else {
                    y++
                }
            }
        }
        println()
        println("After")
        idArray.print()
    }


    fun normalise() {
        val rowSums = (0..(size - 1)).associate { it to getRow(it).filter { it != -1f && it != Float.POSITIVE_INFINITY }.sum() }.toMap()
        transform { x, _, value -> value / rowSums[x]!! }
    }

    private fun swap(x: Int, yA: Int, yB: Int) {
        val desirabilityTemp = get(x, yA)
        set(x, yA, get(x, yB))
        set(x, yB, desirabilityTemp)

        val idTemp = idArray.get(x, yA)
        idArray.set(x, yA, idArray.get(x, yB))
        idArray.set(x, yB, idTemp)
    }

    private fun getActual(x: Int, abstractY: Int): Int {
        return idArray.get(x, abstractY).toInt()
    }

    /**
     * Returns the node that the ant should move to
     */
    fun moveFrom(x: Int, options: List<Int>): Int {
        val newOptions = options.map { getActual(x, it) }
        val abstract = if (newOptions.map { get(x, it) }.contains(Float.POSITIVE_INFINITY)) {
            infinityRandom(x, newOptions)
        }
        else {
            weightedRandom(x, newOptions)
        }
        return getActual(x, abstract)
    }

    override fun weightedRandom(x: Int, options: List<Int>): Int {
        if (options.size == 1) {
            return options.first()
        }

        val max = options.sumByDouble { get(x, it).toDouble() }
        val rand10 = rand.nextDouble()
        val random = (rand10 * max)

        var runningTotal = 0f
        for (y in options) {
            val value = get(x, y)
            runningTotal += value
            if (runningTotal >= random) {
                if (x != y) {
                    return y
                }
            }
        }
        // This should never happen, but can happen veeeeeery rarely due to floating point errors
        println("WARNING: random value was greater than running total after exhausting all options")
        return options.last()
    }

    fun infinityRandom(x: Int, options: List<Int>): Int {
        val infiniteOptions = getRow(x).withIndex().filter {
            (it.value == Float.POSITIVE_INFINITY) &&
                    (it.index != x) &&
                    (it.index in options)
        }.map { it.index }

        return pheremones.weightedRandom(x, infiniteOptions)
    }
}

private class CullingArray(size: Int) : FastTriangularArray(size, { _, _ -> 0f }) {
    fun cull(x: Int, y: Int) {
        set(x, y, 1f)
    }

    fun isCulled(x: Int, y: Int): Boolean = get(x, y) == 1f
}
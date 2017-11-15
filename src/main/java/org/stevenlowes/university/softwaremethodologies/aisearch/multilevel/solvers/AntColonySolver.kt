package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers

import org.stevenlowes.university.softwaremethodologies.aisearch.DistanceArray
import org.stevenlowes.university.softwaremethodologies.aisearch.FastSquareArray
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
                                pheremonesInfluence: Double) {
    val size = distances.size

    val desirabilityArray = FastSquareArray(distances.size, { x, y ->
        val cull = culling.isCulled(x, y)

        if (cull) {
            -1f
        }
        else {
            val distanceValue = distances.get(x, y).toDouble()
            val invDist = 1 / distanceValue
            val distPow = Math.pow(invDist, distanceInfluence)
            val pheremoneValue = pheremones.get(x, y).toDouble()
            val pherPow = Math.pow(pheremoneValue, pheremonesInfluence)
            (pherPow * distPow).toFloat()
        }

    })

    private val idArray = FastSquareArray(distances.size, { x, _ -> x.toFloat() })

    fun compact() {
        println()
        println("Before:")
        idArray.print()
        (0..(size - 1)).forEach { y ->
            var swapPoint = size - 1
            var x = 0
            while (x < swapPoint) {
                val desirability = desirabilityArray.get(x, y)
                if (desirability == -1f) {
                    swap(x, y, swapPoint)
                    println("Swapping $x, $y")

                    swapPoint--
                    while(desirabilityArray.get(swapPoint, y) == -1f){
                        swapPoint--
                    }
                }
                else {
                    x++
                }
            }
        }
        println()
        println("After")
        idArray.print()
    }

    private fun swap(y: Int, xA: Int, xB: Int) {
        val desirabilityTemp = desirabilityArray.get(xA, y)
        desirabilityArray.set(xA, y, desirabilityArray.get(xB, y))
        desirabilityArray.set(xB, y, desirabilityTemp)

        val idTemp = idArray.get(xA, y)
        idArray.set(xA, y, idArray.get(xB, y))
        idArray.set(xB, y, idTemp)
    }

    private fun getActual(abstractX: Int, y: Int): Int {
        return idArray.get(abstractX, y).toInt()
    }

    /**
     * Returns the node that the ant should move to
     */
    fun moveFrom(y: Int, options: List<Int>): Int {
        val newOptions = options.map { getActual(it, y) }
        val abstractX = if (newOptions.map { desirabilityArray.get(it, y) }.contains(Float.POSITIVE_INFINITY)) {
            infinityRandom(y, newOptions)
        }
        else {
            weightedRandom(y, newOptions)
        }
        return getActual(abstractX, y)
    }

    companion object {
        val rand = Random()
    }

    fun weightedRandom(y: Int, options: List<Int>): Int {
        if (options.size == 1) {
            return options.first()
        }

        val abstractOptions = options.map { x -> getActual(x, y) }.sorted().map { x ->
            x to desirabilityArray.get(x,
                                       y)
        }.toMap()
        val max = abstractOptions.values.sum()

        val random = rand.nextFloat() * max

        var runningTotal = 0f
        for ((abstractX, value) in abstractOptions) {
            runningTotal += value
            if (runningTotal >= random) {
                return abstractX
            }
        }
        // This should never happen, but can happen veeeeeery rarely due to floating point errors
        println("WARNING: random value was greater than running total after exhausting all options")
        return options.last()
    }

    fun infinityRandom(y: Int, options: List<Int>): Int {
        val infiniteOptions = desirabilityArray.getRow(y).withIndex().filter {
            (it.value == Float.POSITIVE_INFINITY) &&
                    (it.index != y) &&
                    (it.index in options)
        }.map { it.index }

        return pheremones.weightedRandom(y, infiniteOptions)
    }
}

private class CullingArray(size: Int) : FastTriangularArray(size, { _, _ -> 0f }) {
    fun cull(x: Int, y: Int) {
        set(x, y, 1f)
    }

    fun isCulled(x: Int, y: Int): Boolean = get(x, y) == 1f
}
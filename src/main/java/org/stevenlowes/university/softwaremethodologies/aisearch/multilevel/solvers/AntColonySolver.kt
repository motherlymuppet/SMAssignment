package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers

import org.stevenlowes.university.softwaremethodologies.aisearch.DistanceArray
import org.stevenlowes.university.softwaremethodologies.aisearch.FastSquareArray
import org.stevenlowes.university.softwaremethodologies.aisearch.FastTriangularArray
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node
import java.math.BigInteger
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class AntColonySolver(val antCount: Int,
                      val distanceInfluence: Double,
                      val pherInfluence: Double,
                      val pherStart: Float,
                      val pherEvaporation: Float,
                      val pherDepositing: Float,
                      val cullingRate: Float,
                      val energy: Int,
                      val minimumEdges: Int) : Solver {
    override fun bestPath(nodes: Collection<Node>): List<Node> {
        val distances = nodes.first().level.array
        val pheremones = Pheremones(distances, pherStart)
        val culling = CullingArray(distances.size)

        var energy = this.energy

        var bestAnt: Ant? = null
        while (energy > 0) {
            println()
            println("Iterating! $energy")
            val total = culling.array.count { it == 1f || it == 0f }
            val culled = culling.array.count { it == 1f }
            val percent = (culled.toDouble()) / (total.toDouble()) * 100
            println("Culled $culled of $total ($percent%)")

            val desirability = DesirabilityArray(distances, pheremones, culling, distanceInfluence, pherInfluence)
            desirability.compact()

            val ants = generateAnts(antCount, desirability, distances)
            println("Finished Generating Ants")
            val depositingAnts = if (bestAnt == null) ants else ants.plus(bestAnt)

            updatePheremones(depositingAnts, pheremones, pherEvaporation, pherDepositing)

            val nonCulled = culling.array.zip(desirability.desirabilityArray.array).withIndex().filter { it.value.first == 0f }.map { it.index to it.value.second }.sortedBy { it.second }.map { (it.first % desirability.size) to (it.first / desirability.size) }
            val cullCount = ((nonCulled.size - minimumEdges) * cullingRate).toInt()
            val toBeCulled = nonCulled.take(cullCount)
            toBeCulled.forEach { (x, y) -> culling.cull(x, y) }

            val newBestAnt = ants.minBy { it.distance }!!

            energy -= 1

            if (bestAnt == null || newBestAnt.distance < bestAnt.distance) {
                energy = this.energy
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
        val size = BigInteger.valueOf(count.toLong()) * BigInteger.valueOf(desirability.size.toLong()) * BigInteger.valueOf(
                desirability.size.toLong())
        if (size > BigInteger.valueOf(100 * 1000 * 1000)) {
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
                add(x, y, pheremoneDepositing)
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
        /*
        println()
        println("Before:")
        idArray.print()
        println()
        desirabilityArray.print()
        */
        (0..(size - 1)).forEach { y ->
            var swapPoint = size - 1
            var x = 0
            while (x < swapPoint) {
                if (culling.isCulled(x, y)) {
                    while (culling.isCulled(swapPoint, y) && x < swapPoint) {
                        swapPoint--
                    }
                    if (x >= swapPoint) {
                        break
                    }

                    swap(y, x, swapPoint)
                    //println("Swapping $x with $swapPoint, on row $y")

                    swapPoint--
                }
                x++
            }
        }
        /*
        println()
        println("After")
        idArray.print()
        println()
        desirabilityArray.print()
        */
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
    fun moveFrom(current: Int, absoluteOptions: List<Int>): Int {
        val abstractOptions = absoluteOptions.map { getActual(it, current) }.sorted()
        val infiniteOptions = absoluteOptions.filter { desirabilityArray.get(getActual(it, current), current ) == Float.POSITIVE_INFINITY }

        val response = if(infiniteOptions.isEmpty()) {
            val abstractX = weightedRandom(current, abstractOptions)
            getActual(abstractX, current)
        }
        else {
            infinityRandom(current, infiniteOptions)
        }

        if(response !in absoluteOptions){
            println("here")
        }
        return response
    }

    companion object {
        val rand = Random()
    }

    fun weightedRandom(y: Int, abstractOptions: List<Int>): Int {
        if (abstractOptions.size == 1) {
            return abstractOptions.first()
        }

        var total = 0f
        for (x in abstractOptions) {
            val value = desirabilityArray.get(x, y)
            if (value == -1f) {
                break
            }
            total += value
        }

        val random = rand.nextFloat() * total

        var runningTotal = 0f
        for (x in abstractOptions) {
            val value = desirabilityArray.get(x, y)
            runningTotal += value
            if (runningTotal >= random) {
                return x
            }
        }
        // This happens occasionally when we are forced to take a culled edge. It is unlikely that an ant taking a culled edge has any chance of being the new best ant, so I don't mind the fact that we essentially just choose randomly with abstractOptions.last()
        //println("WARNING: random value was greater than running total after exhausting all abstractOptions")
        return abstractOptions.last()
    }

    fun infinityRandom(y: Int, infiniteOptions: List<Int>): Int {
        return pheremones.weightedRandom(y, infiniteOptions)
    }
}

private class CullingArray(size: Int) : FastTriangularArray(size, { x, y ->
    if (x == y) {
        1f
    }
    else {
        0f
    }
}) {
    fun cull(x: Int, y: Int) {
        set(x, y, 1f)
    }

    fun isCulled(x: Int, y: Int): Boolean = get(x, y) == 1f
}
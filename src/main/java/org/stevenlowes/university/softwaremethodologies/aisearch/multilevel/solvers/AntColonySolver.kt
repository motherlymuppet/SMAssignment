package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers

import org.stevenlowes.university.softwaremethodologies.aisearch.FastSquareArray
import org.stevenlowes.university.softwaremethodologies.aisearch.NodeArray
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node
import java.util.*

class AntColonySolver(val ants: Int, val evaporationRate: Float, val expectedMaxDistance: Float) : Solver {
    override fun bestPath(nodes: Collection<Node>): List<Node> {
        val distances = nodes.first().level.array
        val size = distances.size
        val pheremones = PheremonesArray(size, 100f)
        //var bestAnt: Ant
        while (true) {
            println("${pheremones.array.count { it == 0f }} avg: ${pheremones.average}")
            val ants: Collection<Ant> = simulateAnts(ants, pheremones, distances)
            dropPheremones(ants, pheremones, expectedMaxDistance)
            pheremones.evaporate(1-evaporationRate)
            //bestAnt =
            pheremones.depositAll(-0.1f)

            if (terminate(ants)) {
                val nodesById = nodes.map { it.id to it }.toMap()
                val bestPath = ants.minBy { it.distance }!!.path.map { nodesById[it]!! }
                return bestPath
            }
        }
    }

    /**
     * Ants drop pheremones on the path they travelled
     */
    private fun dropPheremones(ants: Collection<Ant>, pheremones: PheremonesArray, expectedMaxDistance: Float) {
        ants.forEach { pheremones.deposit(it, expectedMaxDistance) }
    }

    /**
     * Simulate all ants,
     */
    private fun simulateAnts(ants: Int, pheremones: PheremonesArray, distances: NodeArray): Collection<Ant> {
        return (1..ants).map { Ant(pheremones, distances) }
    }

    /**
     * Return true if the solver should terminate
     */
    private fun terminate(ants: Collection<Ant>): Boolean{
        val first = ants.first().path
        return ants.all { Arrays.equals(it.path, first) }
    }

    private class Ant(pheremones: PheremonesArray, distances: NodeArray) {
        companion object {
            val rand = Random()
        }

        val path: IntArray
        val distance: Float

        init {
            val size = distances.size
            val array = IntArray(size, {-1})
            val first = rand.nextInt(size)
            array[0] = first
            for(i in 0..(size - 2)){
                val next = pheremones.moveFrom(array[i], array)
                array[i+1] = next
            }

            path = array
            distance = distances.getDistance(array)
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
                    add(previous, node, distance/expectedMaxDistance)
                }
                previous = node
            }
        }

        fun moveFrom(node: Int, visited: IntArray): Int{
            val options: FloatArray = getRow(node)
            val sum = (0..(options.size-1))
                    .filter { it !in visited }
                    .map { options[it] }
                    .sum()
            val randomNum = rand.nextFloat() * sum

            var total = 0f
            for(i in 0..(options.size-1)){
                if(i in visited){
                    continue
                }
                else{
                    total += options[i]
                    if(total > randomNum){
                        return i
                    }
                }
            }

            return (0..(options.size-1)).last { it !in visited }
        }

    }
}
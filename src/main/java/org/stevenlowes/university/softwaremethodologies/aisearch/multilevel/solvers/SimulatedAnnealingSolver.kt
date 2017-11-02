package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers

import org.stevenlowes.university.softwaremethodologies.aisearch.input.Path
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node
import java.util.*

class SimulatedAnnealingSolver(val startTemp: Double, val endTemp: Double, val pow: Double, val mult: Int, val const: Int) : Solver {
    val random = Random()

    override fun bestPath(start: Node, inbetween: Collection<Node>, end: Node): List<Node> {
        val currentState = inbetween.plus(start).plus(end).toMutableList()
        if (currentState.size > 1) {
            val steps = Math.pow(inbetween.size.toDouble(), pow).toInt() * mult + const
            //val tempFactor = Math.pow(endTemp/startTemp, 1.0/steps)
            val tempFactor = 0.9999
            var step = 0
            var temp = startTemp

            var currentValue = evaluate(currentState)

            while (temp > endTemp) {
                if (step % (1000 * 1000) == 0) {
                    println("Simulated Annealing Step $step of $steps current value $currentValue")
                }

                val swapIndices = getSwapIndices(currentState)
                swap<Node>(currentState, swapIndices)
                val newValue = evaluate(currentState)

                if (doSwap(currentValue, newValue, temp)) {
                    currentValue = newValue
                }
                else{
                    swap<Node>(currentState, swapIndices)
                }

                temp *= tempFactor
                step++
            }

            return currentState
        }
        else {
            return currentState.toList()
        }
    }

    private fun doSwap(currentValue: Float, newValue: Float, temperature: Double): Boolean {
        return if (newValue < currentValue) {
            true
        }
        else random.nextDouble() < Math.exp(-(newValue - currentValue) / temperature)
    }

    private fun evaluate(nodes: List<Node>): Float {
        return Path(nodes).distance
    }

    private fun <E> getSwapIndices(list: MutableList<E>): Pair<Int, Int>{
        val index1 = random.nextInt(list.size)
        val index2Temp = random.nextInt(list.size - 1)
        val index2 = if (index2Temp >= index1) {
            index2Temp + 1
        }
        else {
            index2Temp
        }

        return index1 to index2
    }

    private fun <E> swap(list: MutableList<Node>,
                         indices: Pair<Int, Int>){
        val storage = list[indices.first]
        list[indices.first] = list[indices.second]
        list[indices.second] = storage
    }
}
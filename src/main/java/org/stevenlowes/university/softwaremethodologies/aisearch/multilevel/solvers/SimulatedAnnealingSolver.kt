package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers

import org.stevenlowes.university.softwaremethodologies.aisearch.input.Path
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node
import java.util.*

class SimulatedAnnealingSolver(val startTemp: Double, val endTemp: Double, val pow: Double, val mult: Int, val const: Int) : Solver {
    val random = Random()

    override fun bestPath(start: Node, inbetween: Collection<Node>, end: Node): List<Node> {
        if (inbetween.size > 1) {
            val steps = Math.pow(inbetween.size.toDouble(), pow).toInt() * mult + const
            //val tempFactor = Math.pow(endTemp/startTemp, 1.0/steps)
            val tempFactor = 0.9999999
            var step = 0
            var temp = startTemp

            var currentState = inbetween.toList()
            var currentValue = evaluate(start, currentState, end)

            while (temp > endTemp) {
                if (step % (1000 * 1000) == 0) {
                    println("Simulated Annealing Step $step of $steps current value $currentValue")
                }

                val newState = swap(currentState)
                val newValue = evaluate(start, newState, end)

                if (doSwap(currentValue, newValue, temp)) {
                    currentState = newState
                    currentValue = newValue
                }
                temp *= tempFactor
                step++
            }

            return listOf(start) + currentState + end
        }
        else {
            return listOf(start) + inbetween + end
        }
    }

    private fun doSwap(currentValue: Float, newValue: Float, temperature: Double): Boolean {
        return if (newValue < currentValue) {
            true
        }
        else random.nextDouble() < Math.exp(-(newValue - currentValue) / temperature)
    }

    private fun evaluate(start: Node, inbetween: List<Node>, end: Node): Float {
        return Path(listOf(start) + inbetween + end).distance
    }

    private fun <E> swap(list: List<E>): List<E> {
        val mutableList = list.toMutableList()
        val index1 = random.nextInt(list.size)
        val index2Temp = random.nextInt(list.size - 1)
        val index2 = if (index2Temp >= index1) {
            index2Temp + 1
        }
        else {
            index2Temp
        }

        mutableList[index1] = list[index2]
        mutableList[index2] = list[index1]

        return mutableList
    }
}
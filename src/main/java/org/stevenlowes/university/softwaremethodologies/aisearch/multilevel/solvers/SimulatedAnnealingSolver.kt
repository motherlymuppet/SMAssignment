package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers

import org.stevenlowes.university.softwaremethodologies.aisearch.input.Path
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node
import java.util.*

class SimulatedAnnealingSolver(val startTemp: Double, val endTemp: Double, val pow: Double, val mult: Int, val const: Int) : Solver {
    private val random = Random()

    override fun bestPath(start: Node, inbetween: Collection<Node>, end: Node): List<Node> {
        val currentState = inbetween.plus(start).plus(end).toMutableList()
        if (currentState.size > 1) {
            val steps = Math.pow(inbetween.size.toDouble(), pow).toInt() * mult + const
            val tempFactor = Math.pow(endTemp/startTemp, 1.0/steps)
            var step = 0
            var temp = startTemp

            var currentValue = evaluate(currentState)

            while (temp > endTemp) {
                if (step % (1000 * 1000) == 0) {
                    println("Simulated Annealing Step $step of $steps current value $currentValue")
                }

                val swapIndices = getSwapIndices(currentState)
                val valueDelta = evaluateSwap(currentState, swapIndices)

                if (doSwap(currentValue, valueDelta, temp)) {
                    swap(currentState, swapIndices)
                    currentValue += valueDelta
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

    private fun evaluate(currentState: MutableList<Node>): Float {
        return Path(currentState).distance
    }

    private fun doSwap(currentValue: Float, valueDelta: Float, temperature: Double): Boolean {
        val newValue = currentValue + valueDelta
        return if (newValue < currentValue) {
            true
        }
        else random.nextDouble() < Math.exp(-(newValue - currentValue) / temperature)
    }

    /**
     * Returns the distance delta of a swap
     */
    private fun evaluateSwap(currentState: List<Node>, swapIndices: Pair<Int, Int>): Float{

        val adjacent = Math.abs(swapIndices.first - swapIndices.second) == 1

        if(adjacent){
            //I'm too lazy to figure out the edge cases here so we just fall back to computing the whole path.
            //It doesn't happen often and it happens less and less with more nodes, so it has less of an effect
            //In the cases where performance matters

            val currentValue = Path(currentState).distance
            val newState = currentState.toMutableList()
            swap(newState, swapIndices)
            val newValue = Path(newState).distance
            val delta = newValue - currentValue
            return delta
        }
        else {
            var delta = 0f

            val index1 = currentState[swapIndices.first]
            val index2 = currentState[swapIndices.second]
            val index1Minus = currentState.getOrNull(swapIndices.first - 1)
            val index1Plus = currentState.getOrNull(swapIndices.first + 1)
            val index2Minus = currentState.getOrNull(swapIndices.second - 1)
            val index2Plus = currentState.getOrNull(swapIndices.second + 1)

            if (index1Minus != null) {
                delta -= index1Minus.distanceTo(index1)
                delta += index1Minus.distanceTo(index2)
            }

            if (index1Plus != null) {
                delta -= index1.distanceTo(index1Plus)
                delta += index2.distanceTo(index1Plus)
            }

            if (index2Minus != null) {
                delta -= index2Minus.distanceTo(index2)
                delta += index2Minus.distanceTo(index1)
            }

            if (index2Plus != null) {
                delta -= index2.distanceTo(index2Plus)
                delta += index1.distanceTo(index2Plus)
            }

            return delta
        }
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

    private fun swap(list: MutableList<Node>,
                     indices: Pair<Int, Int>){
        val storage = list[indices.first]
        list[indices.first] = list[indices.second]
        list[indices.second] = storage
    }
}
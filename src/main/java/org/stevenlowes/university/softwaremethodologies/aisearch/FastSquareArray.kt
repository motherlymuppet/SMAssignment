package org.stevenlowes.university.softwaremethodologies.aisearch

import java.util.*

open class FastSquareArray(val size: Int, val initialiser: (Int, Int) -> Float) {
    val array: FloatArray = FloatArray(size * size, init = {
        val x = it % size
        val y = it / size
        initialiser(x, y)
    })

    open fun get(x: Int, y: Int): Float = array[y * size + x]

    companion object {
        val rand = Random()
    }

    open fun set(x: Int, y: Int, value: Float) {
        array[y * size + x] = value
    }

    open fun averageDistanceTo(id: Int): Float {
        return getRow(id).average().toFloat()
    }

    open fun transform(transformation: (Int, Int, Float) -> Float) {
        for (x in 0..(size - 1)) {
            for (y in 0..(size - 1)) {
                set(x, y, transformation(x, y, get(x, y)))
            }
        }
    }

    open fun add(x: Int, y: Int, increase: Float) {
        array[y * size + x] += increase
    }

    open fun getRow(y: Int): FloatArray {
        return (0..(size - 1)).map { get(it, y) }.toFloatArray()
    }

    fun weightedRandom(y: Int, options: List<Int>): Int {
        if(options.size == 1){
            return options.first()
        }

        val max = options.map { get(it, y) }.filter { it != Float.NEGATIVE_INFINITY }.sum().toDouble()
        val rand10 = rand.nextDouble()
        val random = (rand10 * max)

        var runningTotal = 0f
        for (x in options) {
            val value = get(x, x)
            runningTotal += value
            if (runningTotal >= random) {
                if (x != x) {
                    return x
                }
            }
        }
        // This should never happen, but can happen veeeeeery rarely due to floating point errors
        println("WARNING: random value was greater than running total after exhausting all options")
        return options.last()
    }

    open val average get() = array.average().toFloat()



    fun print() {
        (0..(size - 1)).forEach {
            println(getRow(it).joinToString())
        }
    }
}
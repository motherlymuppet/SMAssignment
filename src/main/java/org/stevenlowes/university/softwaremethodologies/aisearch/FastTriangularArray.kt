package org.stevenlowes.university.softwaremethodologies.aisearch

import java.util.*

open class FastTriangularArray(val size: Int, val initialiser: (Int, Int) -> Float) {
    val array: FloatArray = FloatArray(size * size, init = {
        val x = it % size
        val y = it / size
        if (x < y) Float.NEGATIVE_INFINITY else initialiser(x, y)
    })

    fun get(x: Int, y: Int): Float{
        val x2 = maxOf(x, y)
        val y2 = minOf(x, y)
        return array[y2 * size + x2]
    }

    companion object {
        val rand = Random()
    }

    fun set(x: Int, y: Int, value: Float){
        val x2 = maxOf(x, y)
        val y2 = minOf(x, y)
        array[y2 * size + x2] = value
    }

    fun averageDistanceTo(id: Int): Float {
        return getRow(id).average().toFloat()
    }

    fun transform(transformation: (Int, Int, Float) -> Float){
        for(x in 0..(size-1)){
            for(y in 0..(size-1)){
                set(x, y, transformation(x, y, get(x, y)))
            }
        }
    }

    fun add(x: Int, y: Int, increase: Float){
        array[x * size + y]+=increase
        array[y * size + x]+=increase
    }

    fun getRow(x: Int): FloatArray{
        return (0..(size - 1)).map { get(x, it) }.toFloatArray()
    }

    fun weightedRandom(x: Int, options: List<Int>): Int {
        /*
        if(options.size == 1){
            return options.first()
        }
        */

        val max = options.map { get(x, it) }.filter { it != Float.NEGATIVE_INFINITY }.sum().toDouble()
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

    val average get() = array.average().toFloat()
}
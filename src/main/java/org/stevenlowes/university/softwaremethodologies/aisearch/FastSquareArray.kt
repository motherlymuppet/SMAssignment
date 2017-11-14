package org.stevenlowes.university.softwaremethodologies.aisearch

import java.util.*

open class FastSquareArray(val size: Int, val initialiser: (Int, Int) -> Float){
    val array: FloatArray = FloatArray(size * size, init = {initialiser(it%size, it/size)})

    fun get(x: Int, y: Int): Float{
        return array[x * size + y]
    }

    companion object {
        val rand = Random()
    }

    fun set(x: Int, y: Int, value: Float){
        array[x*size + y] = value
        array[y*size + x] = value
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

    fun print() {
        (0..(size - 1)).forEach { println(getRow(it).joinToString()) }
    }

    fun add(x: Int, y: Int, increase: Float){
        array[x * size + y]+=increase
        array[y * size + x]+=increase
    }

    fun getRow(x: Int): FloatArray{
        val startIndex = x * size
        val endIndex = (x + 1) * size
        return array.copyOfRange(startIndex, endIndex)
    }

    open fun weightedRandom(x: Int, options: List<Int>): Int {
        if(options.size == 1){
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

    val average get() = array.average().toFloat()
}
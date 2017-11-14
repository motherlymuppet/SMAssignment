package org.stevenlowes.university.softwaremethodologies.aisearch

import java.util.*

open class FastTriangularArray(size: Int, initialiser: (Int, Int) -> Float) : FastSquareArray(size, { x, y ->
    if (x < y) {
        Float.NEGATIVE_INFINITY
    }
    else {
        val x2 = maxOf(x, y)
        val y2 = maxOf(x, y)
        initialiser(x2, y2)
    }
}) {

    override fun get(x: Int, y: Int): Float {
        val x2 = maxOf(x, y)
        val y2 = minOf(x, y)
        return super.get(x2, y2)
    }

    companion object {
        val rand = Random()
    }

    override fun set(x: Int, y: Int, value: Float) {
        val x2 = maxOf(x, y)
        val y2 = minOf(x, y)
        super.set(x2, y2, value)
    }

    override fun averageDistanceTo(id: Int): Float {
        return getRow(id).filter { it != Float.NEGATIVE_INFINITY }.average().toFloat()
    }

    override fun add(x: Int, y: Int, increase: Float) {
        val x2 = maxOf(x, y)
        val y2 = minOf(x, y)
        super.add(x2, y2, increase)
    }

    override fun getRow(x: Int): FloatArray {
        return getRow(x).filter { it != Float.NEGATIVE_INFINITY }.toFloatArray()
    }

    override val average get() = array.filterNot { it == Float.NEGATIVE_INFINITY }.average().toFloat()
}
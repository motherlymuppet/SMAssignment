package org.stevenlowes.university.softwaremethodologies.aisearch

import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node

class FastSquareArray(val size: Int, distances: Map<Node, Map<Node, Float>>){
    val array: Array<Float> = Array(size * size, {0f})

    init {
        val values = distances.map { it.key.id to it.value.map { it.key.id to it.value }.toMap() }.toMap()
        values.forEach { x, map ->
            map.forEach { y, dist ->
                val index = x * size + y
                val index2 = y * size + x
                array[index] = dist
                array[index2] = dist
            }
        }
    }

    fun get(x: Int, y: Int): Float{
        return array[x*size + y]
    }

    val average get() = array.average().toFloat()
}
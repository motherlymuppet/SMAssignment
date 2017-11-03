package org.stevenlowes.university.softwaremethodologies.aisearch

import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node

class NodeArray(size: Int, distances: Map<Int, Map<Int, Float>>): FastSquareArray(size,
    {
        x, y->
        val attempt1 = distances.get(x)?.get(y)
        if(attempt1 == null){
            val attempt2 = distances.get(y)?.get(x)
            attempt2?:0f
        }
        else{
            attempt1
        }
    }) {

    fun getDistance(array: IntArray): Float {
        var prevId: Int? = null
        var total = 0f
        for(id in array){
            if(prevId != null){
                total += get(prevId, id)
            }
            prevId = id
        }
        return total
    }
}
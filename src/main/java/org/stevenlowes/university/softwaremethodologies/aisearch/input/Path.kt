package org.stevenlowes.university.softwaremethodologies.aisearch.input

import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node

data class Path(val nodes: List<Node>) {
    val distance: Float by lazy {
        val indexed = nodes.withIndex().toList()
        val pairs = indexed.map {
            val index = it.index
            val first = indexed[index]
            val second = if (indexed.size > index + 1) indexed[index + 1] else null
            if (second == null) { //This prevents the ArrayIndexOutOfBounds exception
                null
            }
            else {
                first.value to second.value
            }
        }.filterNotNull()

        val distances = pairs.map { (first, second) -> first.distanceTo(second) }
        val sum = distances.sum()
        return@lazy sum
    }
}
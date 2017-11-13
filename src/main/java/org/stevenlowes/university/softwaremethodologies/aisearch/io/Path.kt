package org.stevenlowes.university.softwaremethodologies.aisearch.io

import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node

data class Path(val nodes: List<Node>) {
    val distance: Float by lazy { calcDistance() }

    private fun calcDistance(): Float = nodes.first().level.array.getDistance(nodes.map { it.id }.toIntArray())
}
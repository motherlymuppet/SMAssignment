package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.groupers

import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Level
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Node

class TempNode(val underlying: Node) {
    var inverseResistance: Float = 0f
    val resistance get() = if (inverseResistance == 0f) 0f else 1 / inverseResistance

    fun connectivity(level: Level): Float {
        return 1 / level.nodes.minus(underlying).map { level.distanceBetween(it, underlying) }.map { 1 / it }.sum()
    }
}
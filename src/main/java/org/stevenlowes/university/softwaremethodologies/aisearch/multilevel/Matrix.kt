package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel

class Matrix(rootLevel: Level, val solver: Solver, val grouper: Grouper) {
    val levels: MutableList<Level> = mutableListOf()

    init {
        levels.add(rootLevel)
    }

    fun createLevel() {
        levels.add(levels.last().createParent())
    }
}
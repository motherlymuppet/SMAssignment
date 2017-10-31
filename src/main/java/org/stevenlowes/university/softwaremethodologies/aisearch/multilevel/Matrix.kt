package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel

import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.groupers.Grouper
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers.Solver

class Matrix(val solver: Solver, val grouper: Grouper) {
    val levels: MutableList<Level> = mutableListOf()

    fun setRootLevel(level: Level) {
        levels.add(level)
    }

    private fun createLevel() {
        levels.add(grouper.group(levels.last()))
    }

    fun createAllLevels() {
        while (levels.last().nodes.size != 1) {
            createLevel()
        }
    }

    fun solve(): List<Node> {
        return solver.solve(levels)
    }
}
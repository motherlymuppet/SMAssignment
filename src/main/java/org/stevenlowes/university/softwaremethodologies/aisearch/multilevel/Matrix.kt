package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel

class Matrix(val rootLevelSolver: RootLevelSolver, val upperLevelSolver: UpperLevelSolver, val grouper: Grouper) {
    val levels: MutableList<Level> = mutableListOf()

    fun setRootLevel(level: Level) {
        levels.add(level)
    }

    fun createLevel() {
        levels.add(grouper.group(levels.last().nodes))
    }

    fun solve(): List<Node> {

    }
}
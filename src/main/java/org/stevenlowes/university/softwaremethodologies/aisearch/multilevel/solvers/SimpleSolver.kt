package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers

import org.stevenlowes.university.softwaremethodologies.aisearch.input.Path
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Level
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Node

class SimpleSolver() : Solver {
    override fun solve(levels: List<Level>): Path {
        val localSolutions: Map<Node, List<Node>>
        val topLevel = levels.last()
        topLevel.nodes.forEach { topLevel.nodes }
    }

    fun solveNode(node: Node): List<Node>? {
        if (node is RootNode) {
            return null
        }
        else {
            return solveNodes(node)
        }
    }
}
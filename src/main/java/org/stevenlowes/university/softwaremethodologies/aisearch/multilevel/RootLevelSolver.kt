package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel

interface RootLevelSolver {
    fun solve(startNode: RootNode, nodes: Set<RootNode>): List<Node>
}

interface UpperLevelSolver {
    fun solve(startNode: UpperNode, nodes: Set<UpperNode>): List<Node>
}
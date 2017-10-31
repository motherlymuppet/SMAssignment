package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel

interface Solver {
    fun solve(startNode: Node, nodes: Set<Node>): List<Node>
}
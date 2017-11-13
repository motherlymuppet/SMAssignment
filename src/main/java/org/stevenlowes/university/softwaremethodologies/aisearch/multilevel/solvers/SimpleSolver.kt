package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers

import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node

class SimpleSolver : Solver {
    override fun bestPath(nodes: Collection<Node>): List<Node> {
        return nodes.toList()
    }
}
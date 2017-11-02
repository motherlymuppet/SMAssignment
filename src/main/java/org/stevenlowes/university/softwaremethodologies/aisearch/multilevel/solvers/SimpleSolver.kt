package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers

import org.stevenlowes.university.softwaremethodologies.aisearch.input.Path
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node

class SimpleSolver : Solver {
    override fun bestPath(start: Node, inbetween: Collection<Node>, end: Node): List<Node> {
        return listOf(start) + inbetween + listOf(end)
    }
}
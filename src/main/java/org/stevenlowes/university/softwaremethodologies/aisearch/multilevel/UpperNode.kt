package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel

class UpperNode(id: Int, level: Level, val childNodes: Set<Node>) : Node(id, level) {
    override fun solve(entryNode: Node): List<Node> {
        assert(entryNode in childNodes)
        return level.matrix.solver.solve(entryNode, childNodes)
    }
}
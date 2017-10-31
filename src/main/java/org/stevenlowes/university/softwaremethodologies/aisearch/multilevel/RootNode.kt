package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel

class RootNode(id: Int, level: Level) : Node(id, level) {
    override fun solve(entryNode: Node): List<Node> {
        assert(this == entryNode)
        return listOf(this)
    }
}
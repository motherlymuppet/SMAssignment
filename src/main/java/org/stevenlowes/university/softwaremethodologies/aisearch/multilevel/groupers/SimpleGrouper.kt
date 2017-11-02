package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.groupers

import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Level
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.SimpleGroupNode

class SimpleGrouper(): Grouper{
    override fun group(level: Level): Level {
        val parent = Level()
        val node: Node = SimpleGroupNode(0, parent, level.nodes)

        val distances : MutableMap<Node, Map<Node, Float>> = mutableMapOf()
        distances.put(node, emptyMap())
        parent.addNodes(distances)

        return parent
    }
}
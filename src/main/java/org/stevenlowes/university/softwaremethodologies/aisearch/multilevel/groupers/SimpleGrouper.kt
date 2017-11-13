package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.groupers

import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Level
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.SimpleGroupNode

class SimpleGrouper(): Grouper{
    override fun group(level: Level): Level {
        val parent = Level(level.id + 1, level.name)
        val node: Node = SimpleGroupNode(0, parent, level.nodes)

        val distances : MutableMap<Node, Map<Node, Float>> = mutableMapOf()
        distances.put(node, emptyMap())
        parent.setNodes(distances)

        return parent
    }
}
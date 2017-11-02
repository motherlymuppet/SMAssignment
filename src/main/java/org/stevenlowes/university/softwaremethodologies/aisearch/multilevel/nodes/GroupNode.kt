package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes

import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Level

open class GroupNode(id: Int,
                level: Level,
                childNodes: Collection<Node>,
                override val entryNode: Node,
                override val exitNode: Node
               ) : Node(id, level, childNodes) {
}

class SimpleGroupNode(id: Int, level: Level, childNodes: Collection<Node>): GroupNode(id, level, childNodes, childNodes.first(), childNodes.last())
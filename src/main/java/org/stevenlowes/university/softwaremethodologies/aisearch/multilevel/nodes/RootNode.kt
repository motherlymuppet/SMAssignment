package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes

import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Level

class RootNode(id: Int, level: Level) : Node(id, level, emptySet()){
    override val entryNode: Node = this
    override val exitNode: Node = this
}
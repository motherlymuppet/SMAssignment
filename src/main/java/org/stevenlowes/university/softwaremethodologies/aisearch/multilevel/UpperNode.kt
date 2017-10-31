package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel

class UpperNode(id: Int, level: Level, val childNodes: Set<Node>) : Node(id, level) {
}
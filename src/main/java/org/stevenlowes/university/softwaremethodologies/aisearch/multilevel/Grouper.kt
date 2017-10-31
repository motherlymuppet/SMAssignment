package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel

interface Grouper {
    fun group(nodes: Set<Node>): Level
}
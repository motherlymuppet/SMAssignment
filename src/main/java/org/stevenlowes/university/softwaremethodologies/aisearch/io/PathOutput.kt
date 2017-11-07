package org.stevenlowes.university.softwaremethodologies.aisearch.io

import java.io.PrintWriter

class PathOutput() {
    companion object {
        fun output(path: Path, name: String) {
            val length = path.nodes.size
            val distance = path.distance
            val nodes = path.nodes.map { (it.id + 1).toString() }.joinToString(",")

            PrintWriter("tour$name.txt").use {
                it.println("NAME = $name,")
                it.println("TOURSIZE = $length,")
                it.println("LENGTH = $distance")
                it.println(nodes)
            }
        }
    }
}
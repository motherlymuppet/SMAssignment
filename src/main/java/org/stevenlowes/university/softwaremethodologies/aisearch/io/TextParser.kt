package org.stevenlowes.university.softwaremethodologies.aisearch.io

import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Level
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.RootNode
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.coroutines.experimental.buildSequence

class TextParser {
    companion object {
        fun parseFile(filename: String): Level {
            val file = File(filename)
            val dirtyInput: String = file.readText(StandardCharsets.UTF_8)
            val cleanInput = dirtyInput.replace(Regex("[ \t\n\r]"), "")
            val delimitedInput = cleanInput.split(",")
            val size = delimitedInput[1].substringAfter("SIZE=").toInt()
            val numberStrings = delimitedInput.drop(2)
            val cleanedNumberStrings = numberStrings.map { it.replace(Regex("[^0-9]"), "") }
            val numbers = cleanedNumberStrings.map { it.toFloat() }
            val level = Level(0)
            createMatrix(size, numbers, level)
            return level
        }

        private fun createMatrix(size: Int, numbers: List<Float>, level: Level) {
            val matrix: MutableMap<Int, Map<Int, Float>> = mutableMapOf()
            var nums = numbers
            ((size - 1).downTo(1)).forEach { edges ->
                val values = nums.take(edges)
                nums = nums.drop(edges)
                val startIndex = size - edges
                val indices = startIndex..(size - 1)
                val map = indices.zip(values).toMap()
                matrix.put(startIndex - 1, map)
            }

            val nodes = (0..(size - 1)).map { RootNode(it, level) }
            val nodeMatrix = matrix.mapKeys { nodes[it.key] as Node }.mapValues { it.value.mapKeys { nodes[it.key] as Node } }

            level.setNodes(nodeMatrix)
        }

        private fun partitionNumbers(numbers: Stack<Float>, size: Int): List<List<Float>> {
            assert((size * (size - 1)) / 2 == numbers.size)
            return (0..(size - 1)).map {
                buildSequence {
                    for (i in 1..(it)) {
                        yield(numbers.pop())
                    }
                }.toList()
            }
        }
    }
}
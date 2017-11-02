package org.stevenlowes.university.softwaremethodologies.aisearch.input

import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Level
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.RootNode
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.coroutines.experimental.buildSequence

class TextParser() {
    companion object {
        fun parseFile(filename: String): Level {
            val file = File(filename)
            val dirtyInput: String = file.readText(StandardCharsets.UTF_8)
            val cleanInput = dirtyInput.replace(Regex("[ \t\n\r]"), "")
            val delimitedInput = cleanInput.split(",")
            val size = delimitedInput[1].substringAfter("SIZE=").toInt()
            val numberStrings = delimitedInput.drop(2)
            val cleanedNumberStrings = numberStrings.map { it.replace(Regex("[^0-9]"), "") }
            val numbers = Stack<Float>()
            numbers.addAll(cleanedNumberStrings.map { it.toFloat() })
            val level = Level()
            createMatrix(size, numbers, level)
            return level
        }

        private fun createMatrix(size: Int, numbers: Stack<Float>, level: Level) {
            val matrix: MutableMap<Node, Map<Node, Float>> = mutableMapOf()
            val partitionedNumbers = partitionNumbers(numbers, size).withIndex()
            partitionedNumbers.forEach {
                val index = size - it.index - 1
                val distances: Map<Node, Float> = ((index + 1)..(size + 1)).zip(it.value).toMap().mapKeys {
                    RootNode(it.key,
                             level)
                }
                val node = RootNode(index, level)
                matrix.put(node, distances)
            }
            level.setNodes(matrix.toMap())
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
package org.stevenlowes.university.softwaremethodologies.aisearch.input

import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.coroutines.experimental.buildSequence

class TextParser() {
    companion object {
        fun parseFile(filename: String): Matrix {
            val file = File(filename)
            val dirtyInput: String = file.readText(StandardCharsets.UTF_8)
            val cleanInput = dirtyInput.replace(Regex("[ \t\n\r]"), "")
            val delimitedInput = cleanInput.split(",")
            val name = delimitedInput[0].substringAfter("NAME=")
            val size = delimitedInput[1].substringAfter("SIZE=").toInt()
            val numberStrings = delimitedInput.drop(2)
            val cleanedNumberStrings = numberStrings.map { it.replace(Regex("[^0-9]"), "") }
            val numbers = Stack<Int>()
            numbers.addAll(cleanedNumberStrings.map { it.toInt() })
            val matrix = createMatrix(name, size, numbers)
            return matrix
        }

        fun createMatrix(name: String, size: Int, numbers: Stack<Int>): Matrix {
            val matrix = Matrix(name)
            val partitionedNumbers = partitionNumbers(numbers, size).withIndex()
            partitionedNumbers.forEach {
                val index = size - it.index
                val distances = (index..size).zip(it.value).toMap()
                matrix.addCity(index, distances)
            }
            return matrix
        }

        fun partitionNumbers(numbers: Stack<Int>, size: Int): List<List<Int>> {
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
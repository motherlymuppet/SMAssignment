package org.stevenlowes.university.softwaremethodologies.aisearch

import org.stevenlowes.university.softwaremethodologies.aisearch.algorithms.Genetic
import org.stevenlowes.university.softwaremethodologies.aisearch.input.TextParser

fun main(args: Array<String>) {
    val matrix = TextParser.parseFile("testFiles/9.txt")
    val genetic = Genetic(matrix = matrix,
                          mutationRate = 30f,
                          generationSurvivors = 20,
                          generations = Integer.MAX_VALUE)
    val best = genetic.run()
    println("Finished!")
    println("Min distance: ${best.distance}. Path: ${best.cities}")
}
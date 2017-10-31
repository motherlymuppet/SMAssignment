package org.stevenlowes.university.softwaremethodologies.aisearch

import org.stevenlowes.university.softwaremethodologies.aisearch.input.TextParser
import org.stevenlowes.university.softwaremethodologies.aisearch.solvers.Genetic

fun main(args: Array<String>) {
    println("Starting to load file")
    val matrix = TextParser.parseFile("testFiles/10.txt")
    println("Done loading file")
    val genetic = Genetic(matrix = matrix,
                          mutationRate = 10f,
                          generationSurvivors = 30,
                          generations = Integer.MAX_VALUE)
    val best = genetic.run()
    println("Finished!")
    println("Min distance: ${best.distance}. Path: ${best.nodes}")
}
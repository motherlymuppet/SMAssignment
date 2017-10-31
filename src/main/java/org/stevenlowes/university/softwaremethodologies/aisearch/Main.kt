package org.stevenlowes.university.softwaremethodologies.aisearch

import org.stevenlowes.university.softwaremethodologies.aisearch.input.TextParser
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Matrix
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.groupers.EnergeticGrouper
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers.SimpleSolver

fun main(args: Array<String>) {
    val rootLevel = TextParser.parseFile("testFiles/10.txt")
    val matrix = Matrix(SimpleSolver(), EnergeticGrouper(2f, 5))
    matrix.setRootLevel(rootLevel)
    matrix.createAllLevels()
    println("Done!")
}
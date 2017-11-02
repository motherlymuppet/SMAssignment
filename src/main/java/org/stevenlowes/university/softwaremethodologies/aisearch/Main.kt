package org.stevenlowes.university.softwaremethodologies.aisearch

import org.stevenlowes.university.softwaremethodologies.aisearch.input.TextParser
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Matrix
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.groupers.EnergeticGrouper
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.groupers.SimpleGrouper
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers.SimpleSolver
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers.SimulatedAnnealingSolver

fun main(args: Array<String>) {
    val rootLevel = TextParser.parseFile("testFiles/10.txt")
    //val solver = SimulatedAnnealingSolver(100.0, 10.0, 2.5, 100, 100000)
    val solver = SimulatedAnnealingSolver(10000.0, 0.0001, 2.5, 100, 100000)
    val grouper = SimpleGrouper()
    //val grouper = EnergeticGrouper(2f, 5)
    val matrix = Matrix(solver, grouper)
    matrix.setRootLevel(rootLevel)
    val startTime = System.nanoTime()
    matrix.createAllLevels()
    val path = matrix.solve()
    val endTime = System.nanoTime()
    println(path)
    println(path.distance)
    println("Done!")
    val dif = (endTime - startTime).toDouble() / 1000 / 1000 / 1000
    println(dif)
}
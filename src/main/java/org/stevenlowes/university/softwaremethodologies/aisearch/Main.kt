package org.stevenlowes.university.softwaremethodologies.aisearch

import org.stevenlowes.university.softwaremethodologies.aisearch.input.TextParser
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Matrix
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.groupers.EnergeticGrouper
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers.SimulatedAnnealingSolver

fun main(args: Array<String>) {
    val rootLevel = TextParser.parseFile("big.txt")
    val solver = SimulatedAnnealingSolver(100.0, 10.0, 1.2, 100, 10000)
    //val grouper = SimpleGrouper()
    val grouper = EnergeticGrouper(4f, 15)
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
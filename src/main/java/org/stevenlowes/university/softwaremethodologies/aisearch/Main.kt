package org.stevenlowes.university.softwaremethodologies.aisearch

import org.stevenlowes.university.softwaremethodologies.aisearch.input.TextParser
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Matrix
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.groupers.SimpleGrouper
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers.AntColonySolver

fun main(args: Array<String>) {
    val rootLevel = TextParser.parseFile("testfiles/1.txt")
    //val solver = SimulatedAnnealingSolver(70.0, 1.0, 1.5, 1000, 1000000)
    val solver = AntColonySolver(10 * 1000, 0.05f, 1000f)
    val grouper = SimpleGrouper()
    //val grouper = EnergeticGrouper(3f, 15)
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
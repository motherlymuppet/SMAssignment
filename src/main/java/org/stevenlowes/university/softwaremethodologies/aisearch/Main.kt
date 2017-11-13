package org.stevenlowes.university.softwaremethodologies.aisearch

import org.stevenlowes.university.softwaremethodologies.aisearch.io.PathOutput
import org.stevenlowes.university.softwaremethodologies.aisearch.io.TextParser
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Matrix
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.groupers.SimpleGrouper
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers.AntColonySolver

fun main(args: Array<String>) {
    val rootLevel = TextParser.parseFile("testfiles/9.txt")
    //val solver = SimulatedAnnealingSolver(70.0, 1.0, 2.0, 1000, 1000000)
    val solver = AntColonySolver(1000, 2.0, 2.0, 0.6f, 1f)
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

    PathOutput.output(path, rootLevel.name)
}
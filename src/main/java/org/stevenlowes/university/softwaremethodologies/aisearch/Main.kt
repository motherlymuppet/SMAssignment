package org.stevenlowes.university.softwaremethodologies.aisearch

import org.stevenlowes.university.softwaremethodologies.aisearch.io.Path
import org.stevenlowes.university.softwaremethodologies.aisearch.io.PathOutput
import org.stevenlowes.university.softwaremethodologies.aisearch.io.TextParser
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Matrix
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.groupers.SimpleGrouper
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers.AntColonySolver

fun main2(args: Array<String>) {
    val rootLevel = TextParser.parseFile("testfiles/10.txt")
    //val solver = SimulatedAnnealingSolver(70.0, 1.0, 2.0, 1000, 1000000)
    val solver = AntColonySolver(535, 2.0, 2.0, 0.2f, 0.6f, 1f, 0.05f, 25, 2000)
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

fun main3(args: Array<String>) {
    val rootLevel = TextParser.parseFile("testfiles/4.txt")
    val nodes = rootLevel.nodes.toList().sortedBy { it.id }
    val path = Path(listOf(11,
                           17,
                           5,
                           3,
                           23,
                           24,
                           19,
                           20,
                           10,
                           12,
                           16,
                           21,
                           18,
                           14,
                           13,
                           4,
                           25,
                           22,
                           7,
                           26,
                           1,
                           6,
                           9,
                           8,
                           2,
                           15).map { nodes[it - 1] })
    //val path = Path(listOf(1,6,3,7,5,8,2,4,9,11,10,12).map { nodes[it-1] })
    println(path.distance)
}

fun main(args: Array<String>) {
    main2(args)
}
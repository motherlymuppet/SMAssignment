package org.stevenlowes.university.softwaremethodologies.aisearch.solvers

import org.stevenlowes.university.softwaremethodologies.aisearch.input.Path
import java.util.*
import java.util.stream.Collectors

class Genetic(val matrix: Matrix, val mutationRate: Float, val generationSurvivors: Int, val generations: Int) {
    companion object {
        val random = Random()
    }

    fun generateRandom(): Path {
        val cities = matrix.cities.shuffle()
        return Path(cities, matrix)
    }

    fun generatePopulation(size: Int): Set<Path> {
        return (1..size).map { generateRandom() }.toSet()
    }

    fun breed(mammy: Path, daddy: Path): Path {
        assert(mammy.matrix == daddy.matrix)
        assert(mammy.matrix == matrix)

        val mPath = mammy.nodes
        val dPath = daddy.nodes

        val size = matrix.cities.size
        assert(mPath.size == size)
        assert(dPath.size == size)

        val mIndices = mPath.withIndex().map { it.value to it.index } //Each pair is the city ID followed by its position in the path
        val dIndices = dPath.withIndex().map { it.value to it.index }.toMap()

        val combined = mIndices.map { it.first to (it.second to dIndices[it.first]!!) }
        //This is each city ID paired with its position in both lists

        //In each inner pair, pick one of the two and discard the other. Mutate the chosen value.
        val pickOne = combined.map {
            val choice = random.nextBoolean()
            val chosen = if (choice) it.second.first else it.second.second
            val mutated = chosen.toFloat() + (random.nextFloat() * mutationRate)
            it.first to mutated
        }

        val sorted = pickOne.sortedBy { it.second }.map { it.first }
        return Path(sorted, matrix)
    }

    /**
     * The population is made entirely of fertile hermaphrodites
     */
    fun breedPopulation(breeders: Collection<Path>): Set<Path> {
        val set = breeders.parallelStream().flatMap { mammy ->
            breeders.stream().map { daddy ->
                breed(mammy, daddy)
            }
        }.collect(Collectors.toSet())
        assert(set.size == (breeders.size * breeders.size))
        return set
    }

    fun simulateGeneration(population: Set<Path>): Set<Path> {
        val survivors = population.sortedBy { it.distance }.take(generationSurvivors)
        val children = breedPopulation(survivors)
        return children
    }

    fun run(): Path {
        var population = generatePopulation(generationSurvivors * generationSurvivors)
        var bestOverall: Path? = null
        var lastChanged: Int = 0
        println("Beginning genetic algorithm...")
        (1..generations).forEach { generation ->
            population = simulateGeneration(population)
            println("Finished generation $generation")
            val best = population.minBy { it.distance }!!
            if (bestOverall == null || best.distance < bestOverall!!.distance) {
                bestOverall = best
                lastChanged = generation
            }
            println("Min distance: ${best.distance}. Path: ${best.nodes}")
            println("")
        }
        return bestOverall!!
    }
}
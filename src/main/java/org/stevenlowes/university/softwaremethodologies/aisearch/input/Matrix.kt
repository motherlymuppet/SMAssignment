package org.stevenlowes.university.softwaremethodologies.aisearch.input

class Matrix(){
    private val distances: MutableMap<UndirectionalPair<Int, Int>, Int> = mutableMapOf()

    /**
     * Add a new city
     *
     * @param distancesToNew the distancesToNew to the new city from all old cities
     */
    fun addCity(distancesToNew: Map<Int, Int>){
        //Assert that the map passed contains all current cities
        assert(currentCities == distancesToNew.keys)

        val newCityId = nextCityId

        val newDistances = distancesToNew.mapKeys { UndirectionalPair(newCityId, it.key) }
        distances.putAll(newDistances)
    }

    fun directDistance(from: Int, to: Int): Int {
        val pair = UndirectionalPair(from, to)
        return distances[pair]!!
    }

    val currentCities: Set<Int> get() = distances.keys.flatMap { listOf(it.first, it.second) }.toSet()

    val nextCityId: Int = currentCities.size
}

data class UndirectionalPair<out A: Any, out B: Any>(val first: A, val second: B){
    override fun hashCode(): Int = first.hashCode() + second.hashCode()

    override fun equals(other: Any?): Boolean {
        if(other is UndirectionalPair<*, *>){
            if(other.first == first && other.second == second){
                return true
            }
            if(other.second == first && other.first == second){
                return true
            }
        }
        return false
    }
}
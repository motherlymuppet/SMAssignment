package org.stevenlowes.university.softwaremethodologies.aisearch.input

data class Path(val cities: List<Int>, val matrix: Matrix) {
    val distance: Int
        get() {
            val indexed = cities.withIndex().toList()
            val pairs = indexed.map {
                val index = it.index
                val first = indexed[index]
                val second = if (indexed.size > index + 1) indexed[index + 1] else null
                if (second == null) { //This prevents the ArrayIndexOutOfBounds exception
                    null
                }
                else {
                    first.value to second.value
                }
            }.filterNotNull()

            val distances = pairs.map { (first, second) -> matrix.directDistance(first, second) }
            val sum = distances.sum()
            return sum
        }
}
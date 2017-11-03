package org.stevenlowes.university.softwaremethodologies.aisearch

open class FastSquareArray(val size: Int, val initialiser: (Int, Int) -> Float){
    val array: FloatArray = FloatArray(size * size, init = {initialiser(it%size, it/size)})

    fun get(x: Int, y: Int): Float{
        return array[x * size + y]
    }

    fun set(x: Int, y: Int, value: Float){
        array[x*size + y] = value
        array[y*size + x] = value
    }

    fun averageDistanceTo(id: Int): Float {
        return getRow(id).average().toFloat()
    }

    fun transform(transformation: (Int, Int, Float) -> Float){
        for(x in 0..(size-1)){
            for(y in 0..(size-1)){
                set(x, y, transformation(x, y, get(x, y)))
            }
        }
    }

    fun add(x: Int, y: Int, increase: Float){
        array[x * size + y]+=increase
        array[y * size + x]+=increase
    }

    fun getRow(x: Int): FloatArray{
        val startIndex = x * size
        val endIndex = (x + 1) * size
        return array.copyOfRange(startIndex, endIndex)
    }

    val average get() = array.average().toFloat()
}
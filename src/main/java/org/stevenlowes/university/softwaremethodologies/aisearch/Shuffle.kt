package org.stevenlowes.university.softwaremethodologies.aisearch

import java.util.*

fun <E> Set<E>.shuffle(): List<E> {
    return toList().shuffle()
}

fun <E> List<E>.shuffle(): List<E> {
    val mutable = toMutableList()
    Collections.shuffle(mutable)
    return mutable.toList()
}
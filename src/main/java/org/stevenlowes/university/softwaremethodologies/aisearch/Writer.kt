package org.stevenlowes.university.softwaremethodologies.aisearch

import com.sun.org.apache.bcel.internal.generic.ReturnaddressType
import java.io.PrintWriter
import java.util.*

fun write(size: Int){
    val name = "Test"
    val edges = size * (size-1) / 2
    val random = Random()
    val sj = StringJoiner(",")
    sj.add("NAME = $name")
    sj.add("SIZE = $size")
    for(i in 1..edges){
        val length = ((Math.abs(random.nextGaussian()) + 1) * 100).toInt()
        sj.add(length.toString())
    }

    val writer = PrintWriter("big.txt", "UTF-8")
    writer.println(sj.toString())
    writer.close()
}

fun main(args: Array<String>){
    write(3500)
}
package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.groupers

import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Level

interface Grouper {
    fun group(level: Level): Level
}
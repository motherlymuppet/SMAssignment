package org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.solvers

import org.stevenlowes.university.softwaremethodologies.aisearch.input.Path
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.Level
import org.stevenlowes.university.softwaremethodologies.aisearch.multilevel.nodes.Node

interface Solver {
    fun solve(topNode: Node): Path {
        var currentPath: List<Node> = listOf(topNode)

        var stillUnfolding = true
        while(stillUnfolding){
            stillUnfolding = false
            currentPath = currentPath.flatMap {
                val solution = solveNode(it)
                if(solution.isEmpty()){
                    //We have reached the bottom level
                    return@flatMap listOf(it)
                }
                else{
                    stillUnfolding = true
                    return@flatMap solution
                }
            }
        }
        return Path(currentPath)
    }

    fun solveNode(node: Node): List<Node>{
        if(node.childNodes.isEmpty()){
            return emptyList()
        }
        else{
            return bestPath(node.childNodes)
        }
    }

    fun bestPath(nodes: Collection<Node>): List<Node>
}
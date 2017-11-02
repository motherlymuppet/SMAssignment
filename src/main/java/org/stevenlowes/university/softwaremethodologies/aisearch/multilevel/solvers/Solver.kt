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
            val children = node.childNodes.toMutableList()
            children.remove(node.entryNode)
            children.remove(node.exitNode)
            return bestPath(node.entryNode, children, node.exitNode)
        }
    }

    fun bestPath(start: Node, inbetween: Collection<Node>, end: Node): List<Node>
}
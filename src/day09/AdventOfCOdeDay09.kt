package day09

import utils.Utils.getLineFromResource
import java.util.*

private fun solve() {

    val line = getLineFromResource("InputDay09.txt")
    val regex = "(\\d+) players; last marble is worth (\\d+) points".toRegex()
    val matchResult = regex.matchEntire(line)
    matchResult?.let {
        val (playerStr, marblesStr) = matchResult.destructured

        val playerCount = playerStr.toInt()
        val marbleCount = marblesStr.toInt()
        val task1 = computeHighestScore(marbleCount, playerCount)
        println("Task 1: $task1")
        val task2 = computeHighestScore(marbleCount * 100, playerCount)
        println("Task 2: $task2 ")
    }
}

fun computeHighestScore(marbleCount: Int, playerCount: Int): Long {
    val list = LinkedList<Int>()
    list.add(0)
    val players = mutableListOf<Long>()

    repeat(playerCount) {
        players.add(0)
    }

    for (marble in 1..marbleCount) {

        if (marble % 23 == 0) {
            list.shiftRight(7)
            val value = list.removeFirst()
            val score = marble + value
            val player = marble % playerCount
            players[player] = players[player] + score
            list.shiftLeft()
        } else {
            list.shiftLeft()
            list.addFirst(marble)
        }
    }

    return players.max()!!

}

private fun LinkedList<Int>.shiftLeft() {
    addFirst(removeLast())
}

private fun LinkedList<Int>.shiftRight(steps: Int) {
    repeat(steps) {
        addLast(removeFirst())
    }
}

fun main(args: Array<String>) {
    println("Day 9:")
    solve()
}
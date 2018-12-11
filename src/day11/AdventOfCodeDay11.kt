package day11

import utils.Loc

private fun solve() {
    val serial = 8199
    val min = 1
    val max = 300
    val cell = 3
    val result = Loc.generateField(Loc(min, min), Loc(max - cell, max - cell)).map { Pair(it, computeSumFor(it, cell, serial)) }.maxBy { it.second }!!
    println("Task 1:  ${result.first.x},${result.first.y} ")


    var highestPower = Pair(Loc(0, 0), 0)
    var bestCellSize = 0
    for (cellSize in min..max) {
        val pair = Loc.generateField(Loc(min, min), Loc(max - cellSize, max - cellSize)).map { Pair(it, computeSumFor(it, cellSize, serial)) }.maxBy { it.second }!!

        if (pair.second > highestPower.second) {
            highestPower = pair
            bestCellSize = cellSize
        } else if (pair.second < 0) {
            println("Task 2:  ${highestPower.first.x},${highestPower.first.y},$bestCellSize")
            break
        }
    }
}

private fun computeSumFor(loc: Loc, cell: Int, serial: Int): Int {
    return Loc.generateField(loc, Loc(loc.x + cell - 1, loc.y + cell - 1))
            .map { cellRackId(it.x, it.y, serial) }.sum()
}

private fun Int.hunderts(): Int {
    return when {
        this < 100 -> 0
        this < 1000 -> this / 100
        else -> {
            val str = this.toString()
            val digitChar = str[str.length - 3]
            val v = (digitChar - 48).toInt()
            v
        }
    }
}

private fun cellRackId(x: Int, y: Int, serialNumber: Int): Int {
    val rackId = x + 10
    val number = (rackId * y + serialNumber) * rackId
    return number.hunderts() - 5
}

fun main(args: Array<String>) {
    println("Day 11:")
    solve()
}
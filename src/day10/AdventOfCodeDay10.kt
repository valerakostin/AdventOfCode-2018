package day10

import utils.Loc
import utils.Utils


data class Velocity(val x: Int, val y: Int)
data class Point(val loc: Loc, val vel: Velocity) {
    fun move() = Point(Loc(loc.x + vel.x, loc.y + vel.y), vel)
    fun moveBack() = Point(Loc(loc.x - vel.x, loc.y - vel.y), vel)


    companion object {
        fun createPoint(rawString: String): Point {
            val start = rawString.indexOf("<") + 1
            val stop = rawString.indexOf(">", start) - 1
            val substring = rawString.substring(start..stop)
            val split = substring.split(",")
            val px = split[0].trim().toInt()
            val py = split[1].trim().toInt()

            val vstart = rawString.indexOf("<", stop + 1) + 1
            val vstop = rawString.indexOf(">", vstart) - 1

            val velocity = rawString.substring(vstart..vstop)
            val split2 = velocity.split(",")
            val vx = split2[0].trim().toInt()
            val vy = split2[1].trim().toInt()

            return Point(Loc(px, py), Velocity(vx, vy))
        }
    }
}

private fun List<Point>.getBounds(): Pair<Loc, Loc> {
    val locations = this.map { it.loc }
    val xMin = locations.minBy { it.x }!!.x
    val yMin = locations.minBy { it.y }!!.y
    val xMax = locations.maxBy { it.x }!!.x
    val yMax = locations.maxBy { it.y }!!.y

    return Pair(Loc(xMin, yMin), Loc(xMax, yMax))
}

private fun solve() {
    val lines = Utils.getLinesFromResource("InputDay10.txt")
    val points = lines.map { Point.createPoint(it) }.toList()
    solve(points)
}

private fun solve(points: List<Point>) {

    var newLoc = points
    var counter = 0

    val (initialMin, initialMax) = newLoc.getBounds()
    var currentWidth = initialMax.x - initialMin.x
    var currentHeight = initialMax.y - initialMin.y

    while (true) {
        val (min, max) = newLoc.getBounds()

        val width = max.x - min.x
        val height = max.y - min.y

        if (currentWidth >= width && currentHeight >= height) {
            currentWidth = width
            currentHeight = height
        } else {
            newLoc = newLoc.map { it.moveBack() }
            println("Task 1:")
            printCurrentLayout(newLoc, min, max)
            println("Task 2: Waiting time ${counter - 1}")
            return
        }
        counter++
        newLoc = newLoc.map { it.move() }
    }
}

private fun printCurrentLayout(newLoc: List<Point>, min: Loc, max: Loc) {
    val map = newLoc.groupBy { it.loc }

    val str = buildString {
        for (x in min.y - 1..max.y + 1) {
            for (y in min.x - 1..max.x + 1) {
                val loc = Loc(y, x)

                if (map.containsKey(loc))
                    append("#")
                else
                    append(" ")
            }
            append("\n")
        }
        append("\n")
    }
    println(str)
}

fun main(args: Array<String>) {
    println("Day 10:")
    solve()
}
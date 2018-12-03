package day03


import utils.Utils

private val RECTANGLE_REGEX = "#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)".toRegex()

private data class Loc(val x: Int, val y: Int)

private data class Rectangle(val id: String, val startX: Int, val startY: Int, val width: Int, val height: Int) {
    companion object {
        fun createRectangle(rawLine: String): Rectangle {
            val result = RECTANGLE_REGEX.matchEntire(rawLine)
            result?.let {
                val (id, x, y, w, h) = result.destructured
                return Rectangle(id, x.toInt(), y.toInt(), w.toInt(), h.toInt())
            }
            return Rectangle("", 0, 0, 0, 0)
        }
    }
}

private fun allUnique(rectangle: Rectangle, cache: Map<Loc, Int>): Boolean {
    for (x in rectangle.startX until rectangle.startX + rectangle.width) {
        for (y in rectangle.startY until rectangle.startY + rectangle.height) {
            val loc = Loc(x, y)
            val count = cache[loc]
            if (count == null)
                break
            else if (count > 1) {
                return false
            }
        }
    }
    return true
}


private fun solve() {
    val rectangles = Utils.getItemsFromResource("InputDay03.txt", Rectangle.Companion::createRectangle)

    val cache = mutableMapOf<Loc, Int>()

    rectangles.forEach {
        for (i in it.startX until it.startX + it.width) {
            for (j in it.startY until it.startY + it.height) {
                val loc = Loc(i, j)
                cache.compute(loc) { _, v -> if (v == null) 1 else v + 1 }
            }
        }
    }
    val count = cache.values.filter { it > 1 }.count()
    println("Task 1: $count")

    val uniqueRectangleId = rectangles.first { allUnique(it, cache) }.id
    println("Task 2: $uniqueRectangleId")
}

fun main(args: Array<String>) {
    println("Day 03:")
    solve()
}
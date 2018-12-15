package day13

import utils.Loc
import utils.Utils

private enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    companion object {
        fun isDirection(c: Char) = c == '>' || c == '<' || c == 'v' || c == '^'

        fun directionOf(c: Char): Direction? {
            return when (c) {
                '>' -> Direction.RIGHT
                '<' -> Direction.LEFT
                '^' -> Direction.UP
                'v' -> Direction.DOWN
                else -> null
            }
        }
    }
}

private data class CartPosition(val loc: Loc, val dir: Direction, private val intersectionCount: Int) {

    private fun nextTurnDirection(): Direction {
        val count = intersectionCount % 3

        return when (dir) {
            Direction.DOWN -> {
                when (count) {
                    0 -> Direction.RIGHT
                    2 -> Direction.LEFT
                    else -> this.dir
                }
            }
            Direction.UP -> {
                when (count) {
                    0 -> Direction.LEFT
                    2 -> Direction.RIGHT
                    else -> this.dir
                }
            }
            Direction.RIGHT -> {
                when (count) {
                    0 -> Direction.UP
                    2 -> Direction.DOWN
                    else -> this.dir
                }
            }
            Direction.LEFT -> {
                when (count) {
                    0 -> Direction.DOWN
                    2 -> Direction.UP
                    else -> this.dir
                }
            }
        }
    }

    fun move(trackData: Map<Loc, Char>): CartPosition {
        return when (dir) {
            Direction.RIGHT -> {
                val nextLoc = Loc(loc.x + 1, loc.y)
                val ch = trackData.getOrDefault(nextLoc, ' ')
                when (ch) {
                    '-' -> CartPosition(nextLoc, dir, intersectionCount)
                    '\\' -> CartPosition(nextLoc, Direction.DOWN, intersectionCount)
                    '/' -> CartPosition(nextLoc, Direction.UP, intersectionCount)
                    '+' -> CartPosition(nextLoc, nextTurnDirection(), this.intersectionCount + 1)
                    else -> this
                }
            }
            Direction.LEFT -> {
                val nextLoc = Loc(loc.x - 1, loc.y)
                val ch = trackData.getOrDefault(nextLoc, ' ')
                when (ch) {
                    '-' -> CartPosition(nextLoc, dir, intersectionCount)
                    '\\' -> CartPosition(nextLoc, Direction.UP, intersectionCount)
                    '/' -> CartPosition(nextLoc, Direction.DOWN, intersectionCount)
                    '+' -> CartPosition(nextLoc, nextTurnDirection(), this.intersectionCount + 1)
                    else -> this
                }
            }
            Direction.UP -> {
                val nextLoc = Loc(loc.x, loc.y - 1)
                val ch = trackData.getOrDefault(nextLoc, ' ')
                when (ch) {
                    '|' -> CartPosition(nextLoc, dir, intersectionCount)
                    '\\' -> CartPosition(nextLoc, Direction.LEFT, intersectionCount)
                    '/' -> CartPosition(nextLoc, Direction.RIGHT, intersectionCount)
                    '+' -> CartPosition(nextLoc, nextTurnDirection(), this.intersectionCount + 1)
                    else -> this
                }
            }
            Direction.DOWN -> {
                val nextLoc = Loc(loc.x, loc.y + 1)
                val ch = trackData.getOrDefault(nextLoc, ' ')
                when (ch) {
                    '|' -> CartPosition(nextLoc, dir, intersectionCount)
                    '\\' -> CartPosition(nextLoc, Direction.RIGHT, intersectionCount)
                    '/' -> CartPosition(nextLoc, Direction.LEFT, intersectionCount)
                    '+' -> CartPosition(nextLoc, nextTurnDirection(), intersectionCount + 1)
                    else -> this
                }
            }
        }
    }
}

private fun removeCartsFromTrack(trackData: MutableMap<Loc, Char>, carts: List<CartPosition>) {
    for (cart in carts) {
        val loc = cart.loc
        val left = trackData.getOrDefault(Loc(loc.x - 1, loc.y), ' ')
        val right = trackData.getOrDefault(Loc(loc.x + 1, loc.y), ' ')
        val top = trackData.getOrDefault(Loc(loc.x, loc.y - 1), ' ')
        val down = trackData.getOrDefault(Loc(loc.x, loc.y + 1), ' ')
        when (cart.dir) {
            Direction.RIGHT, Direction.LEFT -> {
                if (top != ' ' && down != ' ')
                    trackData[loc] = '+'
                else
                    trackData[loc] = '-'
            }
            Direction.DOWN, Direction.UP -> {
                if (left != ' ' && right != ' ' && left != '|' && right != '|')
                    trackData[loc] = '+'
                else
                    trackData[loc] = '|'
            }
        }
    }
}

private fun collectCarts(trackData: Map<Loc, Char>): List<CartPosition> {
    return trackData.entries.filter { Direction.isDirection(it.value) }
            .map { CartPosition(it.key, Direction.directionOf(it.value)!!, 0) }.toList()
}

private fun collectTrackData(rawData: List<String>): MutableMap<Loc, Char> {
    val supportedElements = setOf('|', '-', '/', '\\', '+', '>', '<', '^', 'v')
    val trackData = mutableMapOf<Loc, Char>()
    for (y in 0 until rawData.size) {
        val line = rawData[y]
        for (x in 0 until line.length) {
            val element = line[x]
            if (supportedElements.contains(element))
                trackData[Loc(x, y)] = element
        }
    }
    return trackData
}

private fun solve() {
    val lines = Utils.getLinesFromResource("InputDay13.txt")

    val trackData = collectTrackData(lines)
    val carts = collectCarts(trackData)
    removeCartsFromTrack(trackData, carts)

    val positions = mutableListOf<CartPosition>()
    positions.addAll(carts)

    while (true) {
        val sorted = positions.sortedWith(compareBy({ it.loc.x }, { it.loc.y }))

        val toRemove = mutableListOf<Loc>()
        for (p in sorted) {
            if (positions.size == 1) {
                val v = positions[0]
                println("Task 2: ${v.loc.x},${v.loc.y}")
                return
            }
            if (!toRemove.contains(p.loc)) {
                val newPosition = p.move(trackData)
                positions.remove(p)
                positions.add(newPosition)
                val values = positions.groupBy { it.loc }.values
                val collision = values.any { it.size != 1 }
                if (collision) {
                    val loc = values.filter { it.size > 1 }
                            .map { it[0].loc }[0]

                    toRemove.add(loc)
                    val currentPositions = positions.filter { it.loc != loc }
                    positions.clear()
                    positions.addAll(currentPositions)

                    if (carts.size - 2 == positions.size)
                        println("Task 1: ${loc.x},${loc.y}")
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    println("Day 13:")
    solve()
}
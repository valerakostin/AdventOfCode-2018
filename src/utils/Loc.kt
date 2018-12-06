package utils

private val LOC_REGEX = "(\\d+), (\\d+)".toRegex()


data class Loc(val x: Int, val y: Int) {
    fun manhattanDistance(p: Loc): Int {
        return Math.abs(p.x - x) + Math.abs(p.y - y)
    }

    companion object {
        fun parseLoc(rawLine: String): Loc {
            val result = LOC_REGEX.matchEntire(rawLine)
            result?.let {
                val (x, y) = result.destructured
                return Loc(x.toInt(), y.toInt())
            }
            return Loc(0, 0)
        }

        fun initialLoc() = Loc(0, 0)

        fun generateField(from: Loc, to: Loc): List<Loc> {
            val list = mutableListOf<Loc>()
            for (x in from.x..to.x)
                for (y in from.y..to.y) {
                    list.add(Loc(x, y))
                }
            return list
        }

        fun getNeighbour(loc: Loc): List<Loc> {
            return listOf(Loc(loc.x + 1, loc.y), Loc(loc.x - 1, loc.y), Loc(loc.x, loc.y - 1), Loc(loc.x, loc.y + 1), Loc(loc.x - 1, loc.y - 1), Loc(loc.x + 1, loc.y + 1), Loc(loc.x + 1, loc.y - 1), Loc(loc.x - 1, loc.y + 1))
        }
    }
}

fun List<Loc>.getRightLowerLoc(): Loc {
    val initial = Loc.initialLoc()
    return this.map { Pair(it, initial.manhattanDistance(it)) }.maxBy { it.second }?.first ?: initial
}
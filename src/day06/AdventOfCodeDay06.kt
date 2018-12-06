package day06


import utils.Loc
import utils.Loc.Companion.getNeighbour
import utils.Utils
import utils.getRightLowerLoc

private data class MinDistance(val distance: Int, val points: MutableList<Loc>)


private fun solve() {
    val points = Utils.getItemsFromResource("InputDay06.txt", Loc.Companion::parseLoc)
    val edgePoint = points.getRightLowerLoc()
    val allLocs = Loc.generateField(Loc.initialLoc(), edgePoint)
    task1(allLocs, points)
    task2(allLocs, points)
}

private fun task1(allLocs: List<Loc>, points: List<Loc>) {
    val cache = computeDistribution(points, allLocs)
    val reversedMap = mutableMapOf<Loc, MutableList<Loc>>()

    cache.entries.filter { it.value.points.size == 1 }.forEach {
        val minPoints = it.value.points

        if (minPoints.size == 1) {
            var list = reversedMap[minPoints[0]]
            if (list == null)
                list = mutableListOf()
            list.add(it.key)
            reversedMap[minPoints[0]] = list
        }
    }
    val sortedByValue = reversedMap.toList().sortedByDescending { (_, v) -> v.size }.toMap()


    sortedByValue.values.forEach {
        if (it.all { locs -> allNeighboursValid(locs, cache.keys) }) {
            println("Task 1: ${it.size} ")
            return
        }
    }
}

private fun allNeighboursValid(loc: Loc, cache: Set<Loc>) = getNeighbour(loc).all { cache.contains(it) }

private fun computeDistribution(points: List<Loc>, allLocs: List<Loc>): MutableMap<Loc, MinDistance> {
    val cache = mutableMapOf<Loc, MinDistance>()
    for (point in points) {
        allLocs.forEach {
            val manhattanDistance = point.manhattanDistance(it)

            var minDistance = cache[it]
            if (minDistance == null) {
                val locs = mutableListOf(point)
                minDistance = MinDistance(manhattanDistance, locs)
                cache[it] = minDistance
            } else {
                if (minDistance.distance > manhattanDistance) {
                    val locs = mutableListOf(point)
                    minDistance = MinDistance(manhattanDistance, locs)
                    cache[it] = minDistance
                } else if (minDistance.distance == manhattanDistance) {
                    minDistance.points.add(point)
                }
            }
        }
    }
    return cache
}

private fun task2(allLocs: List<Loc>, points: List<Loc>) {
    val regionSize = computeRegionSize(points, allLocs)
    println("Task 2: $regionSize")
}

private fun computeRegionSize(points: List<Loc>, allLocs: List<Loc>): Int {
    return allLocs
            .map { p ->
                points
                        .map { it.manhattanDistance(p) }
                        .sum()
            }
            .filter { it < 10_000 }
            .count()
}


fun main(args: Array<String>) {
    println("Day 06:")
    solve()
}

package day12

import utils.Utils
import java.util.*


private fun solve() {
    val lines = Utils.getLinesFromResource("InputDay12.txt")

    val notes = lines.subList(2, lines.size)
    val mapping = mapping(notes)

    val task1 = doSimulation(initialState(lines[0]), mapping, 20)
    println("Task 1 : $task1")
    val task2 = doSimulation(initialState(lines[0]), mapping, 50000000000)
    println("Task 2 : $task2")
}

private fun doSimulation(currentGeneration: LinkedList<Char>, mapping: Set<String>, generations: Long): Long {
    var globalOffset = 0L

    val history = LinkedList<Char>()

    for (i in 1..generations) {

        history.clear()
        history.addAll(currentGeneration)

        val offset = 4

        adjustHeadAndTail(currentGeneration, 4)

        produceNextGeneration(currentGeneration, mapping)

        // remove leading '.'
        var localOffset = offset
        while (currentGeneration[0] != '#') {
            currentGeneration.removeFirst()
            localOffset--
        }
        if (localOffset != 0)
            globalOffset -= localOffset

        // remove tailing '.'
        while (currentGeneration.last != '#')
            currentGeneration.removeLast()

        // stop after n generations or if sequence does not change
        if (i == generations || history == currentGeneration) {
            val currentOffset = generations - (i - globalOffset)
            return (0 until currentGeneration.size).filter { currentGeneration[it] == '#' }
                    .map { it + currentOffset }.sum()
        }
    }
    return 0
}

private fun adjustHeadAndTail(currentGeneration: LinkedList<Char>, offset: Int) {
    repeat(offset) { currentGeneration.addFirst('.') }
    repeat(offset) { currentGeneration.addLast('.') }
}


fun produceNextGeneration(currentGeneration: LinkedList<Char>, mapping: Set<String>): LinkedList<Char> {

    var counter = currentGeneration.size - 3
    while (counter >= 0) {
        val line = currentGeneration.getLine(0)
        val char = if (mapping.contains(line)) '#' else '.'
        currentGeneration.removeFirst()
        currentGeneration.addLast(char)
        counter--
    }
    return currentGeneration
}

private fun LinkedList<Char>.getLine(position: Int): String {
    return this.substring(position, position + 5)
}

private fun LinkedList<Char>.substring(from: Int, to: Int): String {
    val sb = StringBuilder()
    this.subList(from, to).forEach { sb.append(it) }
    return sb.toString()
}

private fun mapping(list: List<String>): Set<String> {
    return list.filter { it.isNotBlank() && it.endsWith("#") }.map { parseMappingString(it) }
            .toSet()
}

private fun parseMappingString(rawString: String): String {
    val indexOf = rawString.indexOf(" ")
    return rawString.substring(0, indexOf)
}

private fun initialState(rawLine: String): LinkedList<Char> {
    val str = rawLine.substring("initial state: ".length)
    val list = LinkedList<Char>()
    str.forEach { list.add(it) }
    return list
}


fun main(args: Array<String>) {
    println("Day 12:")
    solve()
}
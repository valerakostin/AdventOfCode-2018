package day05

import utils.Utils.getLineFromResource

private fun Char.equalIgnoreCase(ch: Char): Boolean = this - 32 == ch || this + 32 == ch

private fun destroyPolymers(items: MutableList<Char>): String {
    var index = 0
    while (items.size - 1 > index && index >= 0) {
        val current = items[index]
        val next = items[index + 1]

        if (current.equalIgnoreCase(next)) {
            items.removeAt(index)
            items.removeAt(index)
            index--
            if (index < 0)
                index = 0
        } else {
            index++
        }
    }
    return buildString {
        items.forEach { append(it) }
    }
}

private fun String.filterChar(ch: Char): MutableList<Char> {
    return this.filter { ch != it.toUpperCase() }.toMutableList()
}

private fun solve() {
    val line = getLineFromResource("InputDay05.txt")
    val items = line.toMutableList()

    val result = destroyPolymers(items)
    println("Task 1: ${result.length}")

    val min = ('A'..'Z').map { line.filterChar(it) }.map { destroyPolymers(it) }.map { it.length }.min()
    println("Task 2: $min")
}

fun main(args: Array<String>) {
    println("Day 05:")
    solve()
}

package day01

import utils.Utils.getLinesFromSources


fun solve() {
    val linesFromSources = getLinesFromSources("InputDay01.txt")
    val numbers = linesFromSources.map { Integer.parseInt(it) }.toList()
    println("Task 1 ${numbers.sum()}")
    println("Task 2 ${task2(numbers)}")
}

fun task2(numbers: List<Int>): Int {
    val set = mutableSetOf(0)
    var currentFrequency = 0
    while (true) {
        for (number in numbers) {
            currentFrequency += number
            if (set.contains(currentFrequency))
                return currentFrequency
            else
                set.add(currentFrequency)
        }
    }
}

fun main(args: Array<String>) {

    println("Day 01:")
    solve()
}
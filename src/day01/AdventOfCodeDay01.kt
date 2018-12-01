package day01

import utils.Utils.getItemsFromResource


fun solve() {
    val numbers = getItemsFromResource("InputDay01.txt", Integer::parseInt)
    println("Task 1 ${numbers.sum()}")
    println("Task 2 ${task2(numbers)}")
}

fun task2(numbers: List<Int>): Int {
    val set = mutableSetOf(0)
    var currentFrequency = 0
    while (true) {
        for (number in numbers) {
            currentFrequency += number
            if (!set.add(currentFrequency))
                return currentFrequency
        }
    }
}

fun main(args: Array<String>) {
    println("Day 01:")
    solve()
}
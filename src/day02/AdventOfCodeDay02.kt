package day02


import utils.Utils.getLinesFromResource


private fun task1(lines: List<String>) {
    var sum2 = 0
    var sum3 = 0

    lines.forEach { line ->
        val histogram = line.groupingBy { it }.eachCount()
        if (histogram.values.contains(2))
            sum2 += 1
        if (histogram.values.contains(3))
            sum3 += 1
    }
    println("$sum2 * $sum3 = ${sum2 * sum3}")
}

private fun task2(lines: List<String>) {
    for (i in 0 until lines[0].length) {
        val set = mutableSetOf<String>()
        for (item in lines) {
            val array = item.toCharArray()
            array[i] = i.toChar()
            val str = String(array)
            if (!set.add(str)) {
                str.filter { it != i.toChar() }.toCharArray().forEach { print(it) }
                break
            }
        }
    }
}


private fun solve() {
    val lines = getLinesFromResource("InputDay02.txt")
    task1(lines)
    task2(lines)
}

fun main(args: Array<String>) {
    println("Day 02:")
    solve()
}
package day14


private fun Int.getDigits(): List<Int> {
    if (this < 10)
        return listOf(this)
    return this.toString().map { it -> Character.getNumericValue(it) }.toList()
}

private fun solve() {
    val board = mutableListOf(3, 7)
    var elf1Recipe = 0
    var elf2Recipe = 1
    val recipesCount = 633601

    while (board.size < 10 + recipesCount) {
        val newRecipe = board[elf1Recipe] + board[elf2Recipe]
        val digits = newRecipe.getDigits()
        board.addAll(digits)

        elf1Recipe = (elf1Recipe + (board[elf1Recipe] + 1)) % board.size
        elf2Recipe = (elf2Recipe + (board[elf2Recipe] + 1)) % board.size
    }
    val task1 = getLastNFromBoard(board, recipesCount, 10)
    println("Task 1: $task1 ")


    val pattern = recipesCount.getDigits()
    while (true) {
        val newRecipe = board[elf1Recipe] + board[elf2Recipe]
        val digits = newRecipe.getDigits()

        for (digit in digits) {
            board.add(digit)
            if (isOffset(board, pattern)) {
                println("Task 2: ${board.size - pattern.size} ")
                return
            }
        }
        elf1Recipe = (elf1Recipe + (board[elf1Recipe] + 1)) % board.size
        elf2Recipe = (elf2Recipe + (board[elf2Recipe] + 1)) % board.size
    }
}

private fun isOffset(board: List<Int>, offset: List<Int>): Boolean {
    if (board.size < 6)
        return false
    for (i in 1..offset.size) {
        if (board[board.size - i] != offset[offset.size - i])
            return false
    }
    return true
}

private fun getLastNFromBoard(board: List<Int>, offset: Int, count: Int): String {
    return buildString {
        for (i in offset until offset + count)
            append(board[i])
    }
}

fun main(args: Array<String>) {
    println("Day 14:")
    solve()
}
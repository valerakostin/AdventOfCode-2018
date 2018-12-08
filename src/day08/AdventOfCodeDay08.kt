package day08

import utils.Utils

private data class Node(val childCount: Int, val metaDataCount: Int) {
    val children = mutableListOf<Node>()
    private val metaData = mutableListOf<Int>()

    fun addChild(n: Node) {
        children.add(n)
    }

    fun metaDataSum() = metaData.sum()

    fun addMetaData(v: Int) {
        metaData.add(v)
    }


    fun getNodeSum(): Int {
        fun hasChildren(): Boolean = children.isNotEmpty()
        if (!hasChildren())
            return metaDataSum()
        var sum = 0
        for (index in metaData) {
            if (index <= childCount) {
                val node = children[index - 1]
                sum += node.getNodeSum()
            }
        }
        return sum
    }

    fun computeMetaDataSum(): Int {
        var result = metaDataSum()
        for (child in children)
            result += child.computeMetaDataSum()
        return result
    }
}

private fun solve() {
    val rawString = Utils.getLinesFromResources("InputDay08.txt")[0]
    val input = rawString.split(" ").map { it.toInt() }

    val root = getRootNode(input)
    buildTree(input, root, 1)

    task1(root)
    task2(root)
}

private fun task2(root: Node) {
    val nodeSum = root.getNodeSum()
    println("Task 2: $nodeSum")
}

private fun task1(root: Node) {
    val sum = root.computeMetaDataSum()
    println("Task 1: $sum")
}

private fun getRootNode(input: List<Int>): Node {
    val childCount = input[0]
    val metaDataCount = input[1]
    val root = Node(childCount, metaDataCount)
    return root
}


private fun buildTree(input: List<Int>, parent: Node, index: Int): Int {

    val childCount = parent.childCount
    var position = index + 1

    for (i in 0 until childCount) {
        val cc = input[position]
        position += 1
        val meta = input[position]
        val node = Node(cc, meta)
        parent.addChild(node)
        position = buildTree(input, node, position)
    }
    for (i in 0 until parent.metaDataCount) {
        parent.addMetaData(input[position])
        position += 1
    }
    return position
}


fun main(args: Array<String>) {
    println("Day 8:")
    solve()
}

package day07

import utils.Utils
import java.util.*
import kotlin.collections.LinkedHashSet

private val REGEX = "Step ([A-Z]) must be finished before step ([A-Z]) can begin.".toRegex()

private data class Edge(val from: Char, val to: Char)

private class WorkerPool(count: Int) {
    var workers = mutableListOf<Worker>()
    val result = mutableSetOf<Char>()
    var time: Int = 0

    init {
        repeat(count)
        {
            workers.add(Worker())
        }
    }

    fun hasCapacity() = workers.any { it.isIdle() }

    fun isReady() = workers.all { it.isIdle() }

    fun processStep() {
        val readyList = mutableListOf<Char>()
        val space = "          "
        print(time.toString().padStart(5, ' '))
        print(space)
        for (worker in workers) {
            if (worker.isIdle()) {
                print(".")
                print(space)
            } else {
                print("${worker.currentChar}")
                print(space)
                val c = worker.processStep()
                if (c != null)
                    readyList.add(c)
            }
        }
        result.forEach { print(it) }
        println()
        result.addAll(readyList)
        time += 1
    }


    fun requiredComponentsReady(requiredComponents: List<Char>?): Boolean {
        return (requiredComponents == null) || requiredComponents.all { result.contains(it) }
    }

    fun addJob(c: Char) {
        val worker = workers.minBy { it.stepRemaining }
        val w = worker?.addTask(c)
        if (w != null)
            result.add(w)
    }

    private class Worker {
        var currentChar = '0'
        var stepRemaining = 0
        var output: Char? = null


        fun isIdle() = stepRemaining == 0


        fun addTask(task: Char): Char? {
            val workPair = task.getWorkPair()
            currentChar = workPair.first
            stepRemaining = workPair.second

            if (output != null) {
                val ready = output
                output = null
                return ready
            }
            return null
        }

        private fun Char.getWorkPair(): Pair<Char, Int> {
            return Pair(this, 60 + this.toUpperCase().toInt() - 64)
        }

        fun processStep(): Char? {
            stepRemaining -= 1
            if (stepRemaining == 0) {
                val ready = currentChar
                currentChar = '0'
                output = ready
                if (output != null)
                    return output
            }
            return null
        }
    }
}


private class Graph {
    private val parentChildMapping = mutableMapOf<Char, MutableList<Char>>()
    private val childParentMapping = mutableMapOf<Char, MutableList<Char>>()

    fun addEdge(edge: Edge) {

        val children = parentChildMapping.getOrPut(edge.from) { mutableListOf() }
        children.add(edge.to)

        val parents = childParentMapping.getOrPut(edge.to) { mutableListOf() }
        parents.add(edge.from)
    }


    private fun getNextChar(list: List<Char>, rev: Map<Char, List<Char>>, processed: Collection<Char>): Char? {
        val nextNChars = getNextNChars(list, rev, processed, 1)
        if (nextNChars.size == 1)
            return nextNChars[0]
        return null
    }

    private fun getNextNChars(list: List<Char>, rev: Map<Char, List<Char>>, processed: Collection<Char>, count: Int): List<Char> {
        val items = mutableListOf<Char>()

        for (ch in list) {
            val parents = rev.getOrDefault(ch, emptyList())
            val result = parents.all { processed.contains(it) }
            if (result)
                items.add(ch)
            if (items.size == count)
                break
        }
        return items
    }

    private fun printHeader(workerCount: Int) {

        val space = "   "

        print("Second")
        print(space)
        for (i in 0 until workerCount) {
            print("Worker ${i + 1}")
            print(space)
        }
        println("Done")
    }


    fun computeStepCount(line: String, workerCount: Int) {
        printHeader(workerCount)
        val workers = WorkerPool(workerCount)

        val queue = LinkedList<Char>()
        line.forEach { queue.add(it) }

        while (queue.isNotEmpty()) {

            val ready = queue.filter { workers.requiredComponentsReady(childParentMapping[it]) }
            for (c in ready) {
                if (workers.hasCapacity()) {
                    workers.addJob(c)
                    queue.remove(c)
                } else {
                    break
                }
            }
            while (queue.none { workers.requiredComponentsReady(childParentMapping[it]) } || !workers.hasCapacity()) {
                workers.processStep()
                if (queue.isEmpty() && workers.isReady()) {
                    workers.processStep()
                    break
                }
            }
        }
    }


    fun computeOperationOrder(): String {
        val result = LinkedHashSet<Char>()
        val queue = LinkedList<Char>(getRoots())

        while (!queue.isEmpty()) {
            queue.sort()
            val currentItem = getNextChar(queue, childParentMapping, result)
            currentItem?.let {
                queue.remove(currentItem)
                result.add(currentItem)
                val connections = parentChildMapping[currentItem]
                connections?.let {
                    queue.addAll(connections)
                }
            }
        }
        return buildString {
            result.forEach { append(it) }
        }
    }

    private fun getRoots(): Collection<Char> {
        val roots = mutableSetOf<Char>()
        roots.addAll(parentChildMapping.keys)
        roots.removeAll(parentChildMapping.values.flatten().toSet())
        return roots
    }
}


private fun createGraph(lines: List<String>): Graph {
    val graph = Graph()

    for (line in lines) {
        val matchEntire = REGEX.matchEntire(line)
        matchEntire?.let {
            val (from, to) = matchEntire.destructured
            val edge = Edge(from[0], to[0])
            graph.addEdge(edge)
        }
    }
    return graph
}

private fun solve() {
    val lines = Utils.getLinesFromResource("InputDay07.txt")
    val graph = createGraph(lines)
    val task1 = graph.computeOperationOrder()
    println("Task 1:$task1")
    println("Task 2:")
    graph.computeStepCount(task1, 5)
}

fun main(args: Array<String>) {
    println("Day 7:")
    solve()
}
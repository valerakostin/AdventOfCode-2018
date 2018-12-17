package day16

import utils.Utils

private class State {
    private val register = intArrayOf(0, 0, 0, 0)

    fun getRegister(index: Int) = register[index]

    fun setRegister(index: Int, value: Int) {
        register[index] = value
    }

    infix fun theSameAs(state: State): Boolean {
        return (0..3).all { getRegister(it) == state.getRegister(it) }
    }

    override fun toString(): String {
        return buildString {
            append("[")
            append(getRegister(0))
            append(", ")
            append(getRegister(1))
            append(", ")
            append(getRegister(2))
            append(", ")
            append(getRegister(3))
            append("]")
        }
    }

    fun copy(): State {
        val state = State()
        for (i in 0..3)
            state.setRegister(i, getRegister(i))
        return state
    }

    companion object {
        fun of(initial0: String, initial1: String, initial2: String, initial3: String): State {
            val state = State()
            state.setRegister(0, initial0.toInt())
            state.setRegister(1, initial1.toInt())
            state.setRegister(2, initial2.toInt())
            state.setRegister(3, initial3.toInt())
            return state
        }
    }
}

private data class Sample(val before: State, val after: State, val command: Int, val parameters: List<Int>) {
    override fun toString(): String {
        return buildString {
            append("Before: $before\n")
            append("$command ${parameters[0]} ${parameters[1]} ${parameters[2]}\n")
            append("After: $after\n")
        }
    }
}

private sealed class Command(val input1: Int, val input2: Int, val output: Int) {
    abstract fun solve(state: State): State

    private open class RegisterCommand(input1: Int, input2: Int, output: Int, val action: (Int, Int) -> Int) : Command(input1, input2, output) {
        override fun solve(state: State): State {
            val op1 = state.getRegister(input1)
            val op2 = state.getRegister(input2)
            val result = action(op1, op2)
            val newState = state.copy()
            newState.setRegister(output, result)
            return newState
        }
    }

    private open class RegisterValueCommand(input1: Int, input2: Int, output: Int, val action: (Int, Int) -> Int) : Command(input1, input2, output) {
        override fun solve(state: State): State {
            val op1 = state.getRegister(input1)
            val op2 = input2
            val result = action(op1, op2)
            val newState = state.copy()
            newState.setRegister(output, result)
            return newState
        }
    }


    class Addr(input1: Int, input2: Int, output: Int) : RegisterCommand(input1, input2, output, { i, j -> i + j })

    class Addi(input1: Int, input2: Int, output: Int) : RegisterValueCommand(input1, input2, output, { i, j -> i + j })

    class Mulr(input1: Int, input2: Int, output: Int) : RegisterCommand(input1, input2, output, { i, j -> i * j })

    class Muli(input1: Int, input2: Int, output: Int) : RegisterValueCommand(input1, input2, output, { i, j -> i * j })

    class Banr(input1: Int, input2: Int, output: Int) : RegisterCommand(input1, input2, output, { i, j -> i and j })

    class Bani(input1: Int, input2: Int, output: Int) : RegisterValueCommand(input1, input2, output, { i, j -> i and j })

    class Borr(input1: Int, input2: Int, output: Int) : RegisterCommand(input1, input2, output, { i, j -> i or j })

    class Bori(input1: Int, input2: Int, output: Int) : RegisterValueCommand(input1, input2, output, { i, j -> i or j })

    class Setr(input1: Int, input2: Int, output: Int) : Command(input1, input2, output) {
        override fun solve(state: State): State {
            val op1 = state.getRegister(input1)
            val newState = state.copy()
            newState.setRegister(output, op1)
            return newState
        }
    }

    class Seti(input1: Int, input2: Int, output: Int) : Command(input1, input2, output) {
        override fun solve(state: State): State {
            val newState = state.copy()
            newState.setRegister(output, input1)
            return newState
        }
    }


    class Gtir(input1: Int, input2: Int, output: Int) : Command(input1, input2, output) {
        override fun solve(state: State): State {
            val register = state.getRegister(input2)
            val newState = state.copy()
            val value = if (input1 > register) 1 else 0
            newState.setRegister(output, value)
            return newState
        }
    }

    class Gtri(input1: Int, input2: Int, output: Int) : Command(input1, input2, output) {
        override fun solve(state: State): State {
            val register = state.getRegister(input1)
            val newState = state.copy()
            val value = if (register > input2) 1 else 0
            newState.setRegister(output, value)
            return newState
        }
    }

    class Gtrr(input1: Int, input2: Int, output: Int) : Command(input1, input2, output) {
        override fun solve(state: State): State {
            val register1 = state.getRegister(input1)
            val register2 = state.getRegister(input2)

            val newState = state.copy()
            val value = if (register1 > register2) 1 else 0
            newState.setRegister(output, value)
            return newState
        }
    }

    class Eqir(input1: Int, input2: Int, output: Int) : Command(input1, input2, output) {
        override fun solve(state: State): State {
            val register = state.getRegister(input2)
            val newState = state.copy()
            val value = if (input1 == register) 1 else 0
            newState.setRegister(output, value)
            return newState
        }
    }

    class Eqri(input1: Int, input2: Int, output: Int) : Command(input1, input2, output) {
        override fun solve(state: State): State {
            val register = state.getRegister(input1)
            val newState = state.copy()
            val value = if (input2 == register) 1 else 0
            newState.setRegister(output, value)
            return newState
        }
    }

    class Eqrr(input1: Int, input2: Int, output: Int) : Command(input1, input2, output) {
        override fun solve(state: State): State {
            val register1 = state.getRegister(input1)
            val register2 = state.getRegister(input2)
            val newState = state.copy()
            val value = if (register1 == register2) 1 else 0
            newState.setRegister(output, value)
            return newState
        }
    }
}


private fun solve() {
    val lines = Utils.getLinesFromResource("InputDay16.txt")
    val samples = parseSamples(lines)

    val sampleMap = mutableMapOf<Sample, MutableSet<Int>>()
    for (sample in samples) {
        val commands = HashSet<Int>()
        for (commandIndex in 0..15) {
            val command = createCommand(sample.parameters, commandIndex)
            val result = command.solve(sample.before)
            if (result theSameAs sample.after) {
                commands.add(commandIndex)
            }
        }
        sampleMap[sample] = commands
    }
    val count = sampleMap.values.filter { it.size >= 3 }.count()
    println("Task1 $count")

    val commandMapping = getCommandMapping(sampleMap)

    val commandOffset = samples.size * 4 + 2
    var currentState = State()

    for (i in commandOffset until lines.size) {
        val s = lines[i]
        val split = s.split(" ")
        val command = split[0]
        val commandIndex = commandMapping[command.toInt()]
        val parameters = listOf(split[1].toInt(), split[2].toInt(), split[3].toInt())

        val cmd = createCommand(parameters, commandIndex!!)
        currentState = cmd.solve(currentState)
    }
    println("Task2 ${currentState.getRegister(0)}")
}

private fun getCommandMapping(sampleMap: MutableMap<Sample, MutableSet<Int>>): Map<Int, Int> {

    val mapping = mutableMapOf<Int, Int>()

    while (mapping.size != 16) {
        val items = sampleMap.filterValues { it.size == 1 }.toMutableMap()
        items.keys.forEach { sampleMap.remove(it) }

        val ready = items.map { it.key.command to it.value.take(1)[0] }.toSet()
        mapping.putAll(ready)
        val toRemove = ready.map { it.second }.toSet()

        sampleMap.entries.forEach { it.value.removeAll(toRemove) }

    }
    return mapping
}


private fun createCommand(parameters: List<Int>, position: Int): Command {
    return when (position) {
        0 -> Command.Addr(parameters[0], parameters[1], parameters[2])
        1 -> Command.Addi(parameters[0], parameters[1], parameters[2])
        2 -> Command.Mulr(parameters[0], parameters[1], parameters[2])
        3 -> Command.Muli(parameters[0], parameters[1], parameters[2])
        4 -> Command.Banr(parameters[0], parameters[1], parameters[2])
        5 -> Command.Bani(parameters[0], parameters[1], parameters[2])
        6 -> Command.Borr(parameters[0], parameters[1], parameters[2])
        7 -> Command.Bori(parameters[0], parameters[1], parameters[2])
        8 -> Command.Setr(parameters[0], parameters[1], parameters[2])
        9 -> Command.Seti(parameters[0], parameters[1], parameters[2])
        10 -> Command.Gtir(parameters[0], parameters[1], parameters[2])
        11 -> Command.Gtri(parameters[0], parameters[1], parameters[2])
        12 -> Command.Gtrr(parameters[0], parameters[1], parameters[2])
        13 -> Command.Eqir(parameters[0], parameters[1], parameters[2])
        14 -> Command.Eqri(parameters[0], parameters[1], parameters[2])
        15 -> Command.Eqrr(parameters[0], parameters[1], parameters[2])
        else -> throw IllegalArgumentException()
    }
}

private fun parseSamples(lines: List<String>): List<Sample> {

    val samples = mutableListOf<Sample>()
    for (i in 0 until lines.size * 4 + 2 step 4) {
        if (!lines[i].startsWith("Before"))
            break

        val before = parseState(lines[i])
        val items = lines[i + 1].trim().split(" ")
        val command = items[0]
        val list = listOf(items[1].toInt(), items[2].toInt(), items[3].toInt())
        val after = parseState(lines[i + 2])

        val sample = Sample(before, after, command.toInt(), list)
        samples.add(sample)
    }
    return samples
}

private fun parseState(str: String): State {
    val start = str.indexOf("[")
    val end = str.indexOf("]")
    val substring = str.substring(start + 1 until end)
    val items = substring.split(", ")


    return State.of(items[0], items[1], items[2], items[3])

}

fun main(args: Array<String>) {
    println("Day 16:")
    solve()
}
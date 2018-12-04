package day04

import utils.Utils

private val LOG_ENTRY = "\\[(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})] (.+)".toRegex()
private val START_ACTION = "Guard #(\\d+) begins shift".toRegex()

private data class SleepData(var id: Int, val date: Date, val sleepTime: Set<Int>)

private fun solve() {
    val logEntries = Utils.getItemsFromResource("InputDay04.txt", ::createLogEntry)


    val workset = logEntries.sortedWith(compareBy({ it.timestamp.date.year }, { it.timestamp.date.month }, { it.timestamp.date.day }, { it.timestamp.time.hour }, { it.timestamp.time.minutes }))

    workset.forEach { println(it) }
    val sleepCalendar = arrayListOf<SleepData>()


    val intervals = mutableListOf<Int>()
    val initial = Date(0, 0, 0)
    var currentDate = initial
    var currentId: Int = -1
    workset.forEach {
        val (date, action) = it

        if (currentId == -1 && action is Action.Start && currentDate == initial) {
            currentId = action.guardId
            if (date.time.hour == 0)
                currentDate = date.date
        } else {

            if (currentDate == initial)
                currentDate = date.date
            when (action) {
                Action.FallsAsleep -> intervals.add(date.time.minutes)
                Action.WakeUp -> intervals.add(date.time.minutes)
                is Action.Start -> {

                    val set = mutableSetOf<Int>()
                    for (i in 0 until intervals.size step 2) {
                        val start = intervals[i]
                        val end: Int = intervals[i + 1]
                        for (minute in start until end)
                            set.add(minute)
                    }
                    sleepCalendar.add(SleepData(currentId, currentDate, set))
                    // clear
                    currentId = action.guardId
                    currentDate = initial
                    intervals.clear()
                }
            }
        }
    }
// add last row
    val set = mutableSetOf<Int>()
    for (i in 0 until intervals.size step 2) {
        val start = intervals[i]
        val end: Int = intervals[i + 1]
        for (minute in start until end)
            set.add(minute)
    }
    sleepCalendar.add(SleepData(currentId, currentDate, set))

    printTable(sleepCalendar)
    solveTask1(sleepCalendar)
    solveTask2(sleepCalendar)
}

private fun solveTask2(sleepCalendar: ArrayList<SleepData>) {
    val groupByGuard = sleepCalendar.groupBy(SleepData::id)

    val map = mutableMapOf<Int, Map.Entry<Int, Int>>()

    for ((key, value) in groupByGuard) {
        val guardData = mutableMapOf<Int, Int>()

        value.flatMap { it.sleepTime }.forEach {
            guardData.compute(it) { _, v -> if (v == null) 1 else v + 1 }
        }
        val max = guardData.maxBy { it.value }
        if (max != null)
            map[key] = max
    }

    val resultData = map.maxBy { it.value.value }
    if (resultData != null)
        println("Task 2: Guard #${resultData.key}  ${resultData.key} * ${resultData.value.key} = ${resultData.key * resultData.value.key}")
}

private fun solveTask1(sleepCalendar: ArrayList<SleepData>) {
    val max = sleepCalendar.groupBy(SleepData::id).mapValues { it -> it.value.map { it.sleepTime.size }.sum() }.maxBy { it.value }


    if (max != null) {
        val guardId = max.key
        val guardSleepData = sleepCalendar.filter { it.id == guardId }

        val sleepDistribution = mutableMapOf<Int, Int>()

        guardSleepData.flatMap { it.sleepTime }.forEach {
            sleepDistribution.compute(it) { _, v -> if (v == null) 1 else v + 1 }
        }

        val maxMinute = sleepDistribution.maxBy { it.value }
        if (maxMinute != null)
            println("Task 1: Guard #$guardId max sleep time  ${max.value},  $guardId * ${maxMinute.key} = ${guardId * maxMinute.key}")
    }
}

private fun printTable(sleepData: List<SleepData>) {

    println("Sleep plan")
    println("Date   ID     Minute")
    println("              000000000011111111112222222222333333333344444444445555555555")
    println("              012345678901234567890123456789012345678901234567890123456789")
    sleepData.forEach {
        print("${it.date.month.toString().padStart(2, '0')}-${it.date.day.toString().padStart(2, '0')}  #${(it.id).toString().padStart(4, '0')}  ")

        val str = buildString {
            for (i in 0..59) {
                if (it.sleepTime.contains(i))
                    append("#")
                else
                    append(".")
            }
        }
        println(str)
    }
}

private sealed class Action {
    object FallsAsleep : Action() {
        override fun toString() = "falls asleep"
    }

    object WakeUp : Action() {
        override fun toString() = "wake up"
    }

    object NoAction : Action() {
        override fun toString() = "no action"
    }

    class Start(val guardId: Int) : Action() {
        override fun toString() = "Guard #$guardId begins shift"
    }
}

private data class Date(val year: Int, val month: Int, val day: Int)
private data class Time(val hour: Int, val minutes: Int)

private data class Timestamp(val date: Date, val time: Time) {
    override fun toString() = "[${date.year}-${(date.month).toString().padStart(2, '0')}-${date.day.toString().padStart(2, '0')} ${time.hour.toString().padStart(2, '0')}:${time.minutes.toString().padStart(2, '0')}]"
}

private data class LogEntry(val timestamp: Timestamp, val action: Action) {
    override fun toString() =
            timestamp.toString() + " " + action.toString()
}


private fun createLogEntry(rawLine: String): LogEntry {
    val result = LOG_ENTRY.matchEntire(rawLine)
    result?.let {
        val (yearString, monthString, dayString, hourString, minutesString, actionString) = result.destructured
        val timestamp = Timestamp(Date(yearString.toInt(), monthString.toInt(), dayString.toInt()), Time(hourString.toInt(), minutesString.toInt()))

        val action: Action = when (actionString) {
            "falls asleep" -> Action.FallsAsleep
            "wakes up" -> Action.WakeUp
            else -> {
                if (actionString.startsWith("Guard")) {
                    val actionMatch = START_ACTION.matchEntire(actionString)
                    if (actionMatch != null) {
                        val (guardId) = actionMatch.destructured
                        Action.Start(guardId.toInt())
                    } else
                        Action.NoAction
                } else {
                    Action.NoAction
                }
            }
        }
        return LogEntry(timestamp, action)
    }
    return LogEntry(Timestamp(Date(0, 0, 0), Time(0, 0)), Action.NoAction)
}

fun main(args: Array<String>) {
    println("Day 04:")
    solve()
}
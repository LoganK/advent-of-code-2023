import java.io.File;

fun main() {
    fun String.extractInt(): Int =
        listOf(first(), last()).joinToString(separator="").toInt()

    fun calibrator(input: String): Int =
        input
            .filter { it.isDigit() }
            .extractInt()
    fun part1(input: List<String>): Int =
        input
            .map { calibrator(it) }
            .sum()

    fun extractDigits(input: String): Pair<Int, Int> {
        val mapping = mapOf(
            "1" to 1,
            "2" to 2,
            "3" to 3,
            "4" to 4,
            "5" to 5,
            "6" to 6,
            "7" to 7,
            "8" to 8,
            "9" to 9,
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9,
        )

        val first: Int = mapping
            .map { entry -> input
                    .windowed(entry.key.length)
                    .withIndex()
                    .firstOrNull { it.value == entry.key }
                    .let { Pair(it?.index ?: input.length, entry) } }
            .minBy { it.first}
            .second.value

        val last: Int = mapping
            .map { entry -> input
                    .windowed(entry.key.length)
                    .withIndex()
                    .lastOrNull { it.value == entry.key }
                    .let { Pair(it?.index ?: -1, entry) } }
            .maxBy { it.first}
            .second.value

        return Pair(first, last)
    }
    fun part2(input: List<String>): Int =
        input
            .map { extractDigits(it) }
            .map { it.first * 10 + it.second }
            .sum()

    val testInput1 = """1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet""".lines()
    println(part1(testInput1))
    val testInput2 = """two1nine
eightwothree
abcone2threexyz
xtwone3four
4nineeightseven2
zoneight234
7pqrstsixteen""".lines()
    println(part2(testInput2))

    val input = File("src/Day01_1.txt").readLines()
    println(part1(input))
    println(part2(input))
}

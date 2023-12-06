import java.io.File
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

data class Race(val time: Long, val record: Long) {
  companion object {
    fun fromStrings(input: List<String>): List<Race> {
      val times = input[0].split(':')[1].split(' ').filter(String::isNotBlank).map(String::toLong)
      val records = input[1].split(':')[1].split(' ').filter(String::isNotBlank).map(String::toLong)
      return times.zip(records).map { Race(it.first, it.second) }
    }

    fun fromStrings2(input: List<String>): List<Race> {
      val time =
          input[0]
              .split(':')[1]
              .split(' ')
              .filter(String::isNotBlank)
              .joinToString(separator = "")
              .toLong()
      val record =
          input[1]
              .split(':')[1]
              .split(' ')
              .filter(String::isNotBlank)
              .joinToString(separator = "")
              .toLong()
      return listOf(Race(time, record))
    }

    private fun quadratic(a: Long, b: Long, c: Long): Pair<Long, Long> {
      val p1 = (b * b - 4 * a * c).toDouble()
      return Pair(
          ceil((-b.toDouble() - sqrt(p1)) / (2 * a)).toLong(),
          floor((-b.toDouble() + sqrt(p1)) / (2 * a)).toLong())
    }
  }

  fun winCount(): Long {
    // record = x * (time - x)
    val zeros = quadratic(-1, time, -record)
    return abs(zeros.second - zeros.first + 1)
  }
}

fun main() {
  fun part1(races: List<Race>): Long = races.map(Race::winCount).reduce { acc, f -> acc * f }

  fun part2(races: List<Race>): Long = races.map(Race::winCount).reduce { acc, f -> acc * f }

  val testStr =
      """
      Time:      7  15   30
      Distance:  9  40  200
        """.trimIndent().lines()
  val inputStr = File("Day06.txt").readLines()

  val test = Race.fromStrings(testStr)
  val input = Race.fromStrings(inputStr)
  println(part1(test))
  println(part1(input))

  val test2 = Race.fromStrings2(testStr)
  val input2 = Race.fromStrings2(inputStr)
  println(part2(test2))
  println(part2(input2))
}

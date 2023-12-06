import java.io.File

data class Race(val time: ULong, val record: ULong) {
  companion object {
    fun fromStrings(input: List<String>): List<Race> {
      val times = input[0].split(':')[1].split(' ').filter(String::isNotBlank).map(String::toULong)
      val records =
          input[1].split(':')[1].split(' ').filter(String::isNotBlank).map(String::toULong)
      return times.zip(records).map { Race(it.first, it.second) }
    }

    fun fromStrings2(input: List<String>): List<Race> {
      val time =
          input[0]
              .split(':')[1]
              .split(' ')
              .filter(String::isNotBlank)
              .joinToString(separator = "")
              .toULong()
      val record =
          input[1]
              .split(':')[1]
              .split(' ')
              .filter(String::isNotBlank)
              .joinToString(separator = "")
              .toULong()
      return listOf(Race(time, record))
    }
  }

  fun winCount(): Int = (1uL..time - 1u).filter { it * (time - it) > record }.count()
}

fun main() {
  fun part1(races: List<Race>): Int = races.map(Race::winCount).reduce { acc, f -> acc * f }

  fun part2(races: List<Race>): Int = races.map(Race::winCount).reduce { acc, f -> acc * f }

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

import java.io.File
import java.math.BigInteger

data class Room(val rows: List<String>, val cols: List<String>) {
  companion object {
    fun String.smudgeDistance(other: String): Int =
      this.zip(other)
        .filter { it.first != it.second }
        .count()

    fun parseRoom(input: List<String>): Room {
      val cols =
          input
              .flatMap(String::withIndex)
              .groupBy({ it.index }, { it.value })
              .map { it.value.joinToString(separator = "") }
      return Room(input, cols)
    }

    fun fromString(input: String): List<Room> {
      return input.split("\n\n").map { parseRoom(it.lines().filter(String::isNotEmpty)) }
    }

    fun findSymmetry(input: List<String>, smudgeCount: Int): Long {
      // Just scan everything.
      for (n in 1..input.size - 1) {
        // Zip conveniently drops non-matching columns.
        val totalDistance = (n..<input.size).zip(n - 1 downTo 0)
          .map { input[it.first].smudgeDistance(input[it.second]) }
          .sum()
        if (totalDistance == smudgeCount) {
          return n.toLong()
        }
      }

      return 0L
    }
  }

  fun calcScore(): Long = findSymmetry(cols, 0) + 100L * findSymmetry(rows, 0)
  fun calcScore2(): Long = findSymmetry(cols, 1) + 100L * findSymmetry(rows, 1)
}

fun main() {
  fun part1(input: List<Room>): Long = input.map(Room::calcScore).sum()
  fun part2(input: List<Room>): Long = input.map(Room::calcScore2).sum()

  val testStr =
      """
      #.##..##.
      ..#.##.#.
      ##......#
      ##......#
      ..#.##.#.
      ..##..##.
      #.#.##.#.
      
      #...##..#
      #....#..#
      ..##..###
      #####.##.
      #####.##.
      ..##..###
      #....#..#
    """
          .trimIndent()
  val inputStr = File("Day13.txt").readText()

  val test = Room.fromString(testStr)
  val input = Room.fromString(inputStr)

  println(part1(test))
  println(part1(input))
  println(part2(test))
  println(part2(input))
}

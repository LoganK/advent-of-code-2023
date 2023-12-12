import java.io.File

data class Room(val rows: Map<String, List<Int>>, val cols: Map<String, List<Int>>) {
  companion object {
    fun parseRoom(input: List<String>): Room {
      // We use a String->Index map to make it easy to find similar strings.
      val rows = input.withIndex().groupBy({ it.value }, { it.index }).toMap()
      val cols =
          input
              .flatMap(String::withIndex)
              .groupBy({ it.index }, { it.value })
              .map { it.value.joinToString(separator = "") to it.key }
              .groupBy({ it.first }, { it.second })
              .toMap()
      return Room(rows, cols)
    }

    fun fromString(input: String): List<Room> {
      return input.split("\n\n").map { parseRoom(it.lines()) }
    }
  }

  fun findSymmetry(input: Map<String, List<Int>>): Int {
    // Anything where two identical strings are adjacent.
    val candidates =
        input.flatMap { entry ->
          entry.value.zipWithNext().filter { it.first + 1 == it.second }.map { it.first + 1 }
        }
    val lastIndex = input.values.map { it.size }.sum() - 1
    for (n in candidates) {
      // Zip conveniently drops non-matching columns.
      if ((n..lastIndex).zip((n - 1 downTo 0)).all { pair ->
        input.values.any { (listOf(pair.first, pair.second) - it).isEmpty() }
      }) {
        return n
      }
    }

    return 0
  }

  fun calcScore(): Long = (findSymmetry(cols) + 100 * findSymmetry(rows)).toLong()
}

fun main() {
  fun part1(input: List<Room>): Long = input.map(Room::calcScore).sum()
  fun part2(input: List<Room>): Long = 1L

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

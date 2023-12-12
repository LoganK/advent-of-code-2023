import java.io.File
import java.math.BigInteger

fun <T> Collection<T>.pairCombinations(): Sequence<Pair<T, T>> = sequence {
  forEachIndexed { i, a -> drop(i + 1).forEach { b -> yield(Pair(a, b)) } }
}

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
      return input.split("\n\n").map { parseRoom(it.lines().filter(String::isNotEmpty)) }
    }
  }

  fun <T> findSymmetry(input: Map<T, List<Int>>): Int {
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

  fun findSmudgeSymmetry(input: Map<String, List<Int>>): Int {
    // Use bits for convenient XOR.
    fun String.toRoomNum(): BigInteger =
        BigInteger(map { if (it == '#') '1' else '0' }.joinToString(separator = ""), 2)
    fun BigInteger.singleBit(): Boolean = (this and (this - BigInteger.ONE)) == BigInteger.ZERO
    val newInput = input.map { it.key.toRoomNum() to it.value }.toMap()

    // Anything where almost identical strings are adjacent.
    val candidateKeys =
        newInput.keys.pairCombinations().filter { (it.first xor it.second).singleBit() }
    for (candidate in candidateKeys) {
      // There could be multiple matches, but we can swap only one candidate.
      for (index in newInput[candidate.second]!!) {
        var remap = newInput.toMutableMap()
        remap[candidate.second] = remap[candidate.second]!!.minusElement(index)
        remap[candidate.first] = remap[candidate.first]!!.plusElement(index)
        val n = findSymmetry(remap)
        if (n > 0) {
          return n
        }
      }
    }

    return 0
  }

  fun calcScore2(): Long = (100 * findSmudgeSymmetry(rows) + findSmudgeSymmetry(cols)).toLong()
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

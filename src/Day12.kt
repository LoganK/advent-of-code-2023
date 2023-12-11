import java.io.File

private class MemoizedCombinations {
  val memoize = mutableMapOf<Pair<String, List<Int>>, Long>()

  fun combinations(record: String, groups: List<Int>): Long =
      memoize.getOrPut(Pair(record, groups), { _combinations(record, groups) })

  // Support for `combinations()` to support recursion.
  private fun _combinations(record: String, groups: List<Int>): Long {
    if (groups.isEmpty()) {
      // The current combination is valid depending on whether or not we have more springs.
      return if (record.indexOf('#') >= 0) 0L else 1L
    }

    val slimRec = record.trim('.')
    if (slimRec.length == 0) {
      // No more combinations to be had.
      return 0L
    }

    val nextGroupLen = groups[0]
    val nextChar = slimRec.getOrNull(nextGroupLen) ?: '.'
    return when (slimRec[0]) {
      '?' ->
          combinations('#' + slimRec.drop(1), groups) + combinations('.' + slimRec.drop(1), groups)
      '#' ->
          if (slimRec.length < nextGroupLen || slimRec.take(nextGroupLen).any { it == '.' }) {
            // Current group too short to start here.
            0L
          } else if (nextChar == '#') {
            // Current group too long to start here.
            0L
          } else if (nextChar == '?') {
            // Need it to not terminate.
            combinations(slimRec.drop(nextGroupLen + 1), groups.drop(1))
          } else {
            combinations(slimRec.drop(nextGroupLen), groups.drop(1))
          }
      else -> throw IllegalStateException("Unknown char '${slimRec[0]}'")
    }
  }
}

data class Spring(val record: String, val groups: List<Int>) {
  companion object {
    fun fromString(input: String): Spring {
      val (mapStr, groupStr) = input.split(' ', limit = 2)
      return Spring(mapStr, groupStr.split(',').map(String::toInt))
    }

    fun fromString(input: List<String>): List<Spring> = input.map(Spring::fromString)

    fun fromString2(input: String): Spring {
      val (mapStr, groupStr) = input.split(' ', limit = 2)
      val mapStr2 = (1..5).map { mapStr }.joinToString(separator = "?")
      val groupStr2 = (1..5).map { groupStr }.joinToString(separator = ",")
      return Spring(mapStr2, groupStr2.split(',').map(String::toInt))
    }

    fun fromString2(input: List<String>): List<Spring> = input.map(Spring::fromString2)
  }

  fun combinations(): Long {
    return MemoizedCombinations().combinations(record, groups)
  }
}

fun main() {
  fun part1(input: List<Spring>): Long = input.map { it.combinations() }.sum()

  fun part2(input: List<Spring>): Long = input.map { it.combinations() }.sum()

  val testStr =
      """
      ???.### 1,1,3
      .??..??...?##. 1,1,3
      ?#?#?#?#?#?#?#? 1,3,1,6
      ????.#...#... 4,1,1
      ????.######..#####. 1,6,5
      ?###???????? 3,2,1
    """
          .trimIndent()
          .lines()
  val inputStr = File("Day12.txt").readLines()

  val test = Spring.fromString(testStr)
  val input = Spring.fromString(inputStr)

  println(part1(test))
  println(part1(input))

  val test2 = Spring.fromString2(testStr)
  val input2 = Spring.fromString2(inputStr)

  println(part2(test2))
  println(part2(input2))
}

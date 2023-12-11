import java.io.File

data class Spring(val record: String, val groups: List<Long>) {
  companion object {
    fun fromString(input: String): Spring {
      val (mapStr, groupStr) = input.split(' ', limit = 2)
      return Spring(mapStr, groupStr.split(',').map(String::toLong))
    }

    fun fromString(input: List<String>): List<Spring> = input.map(Spring::fromString)
  }

  fun combinations(): Sequence<Spring> = sequence {
    // Brute force all combinations.
    val questions = record.filter { it == '?' }.count()
    assert(questions < 64)
    for (guess in (0 ..< (1L shl questions))) {
      val fill = guess.toString(radix = 2).reversed().toMutableList()
      yield(
          Spring(
              record
                  .map {
                    when (it) {
                      '?' ->
                          when (fill.removeFirstOrNull() ?: '0') {
                            '0' -> '.'
                            else -> '#'
                          }
                      else -> it
                    }
                  }
                  .joinToString(separator = ""),
              groups))
    }
  }

  fun calcGroups(): List<Long> =
      record.split('.').filterNot(String::isEmpty).map { it.length.toLong() }

  fun valid(): Boolean = calcGroups() == groups
}

fun main() {
  fun part1(input: List<Spring>): Long =
      input.map { it.combinations().filter(Spring::valid).count().toLong() }.sum()

  fun part2(input: List<Spring>): Long = 1L

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

  println(part2(test))
  println(part2(input))
}

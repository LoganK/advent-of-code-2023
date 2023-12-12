import java.io.File
import kotlin.math.abs

data class Point(val x: Long, val y: Long) {
  fun distance(other: Point): Long =
    abs(x - other.x) + abs(y - other.y)
}

fun <T> List<T>.pairCombinations(): Sequence<Pair<T, T>> = sequence {
  forEachIndexed { i, a -> drop(i + 1).forEach { b -> yield(Pair(a, b)) } }
}

data class Galaxy(val points: List<Point>) {
  companion object {
    fun expanded(input: List<Point>, expansion: Long): List<Point> {
      var expanded = input
      // Horizontal expansion
      val xSize = input.maxOf { it.x }
      for (x in xSize downTo 0) {
        if (input.none { it.x == x }) {
          expanded = expanded.map { if (it.x > x) it.copy(x = it.x + expansion - 1) else it }
        }
      }

      // Vertical expansion
      val ySize = input.maxOf { it.y }
      for (y in ySize downTo 0) {
        if (input.none { it.y == y }) {
          expanded = expanded.map { if (it.y > y) it.copy(y = it.y + expansion - 1) else it }
        }
      }

      return expanded
    }

    fun fromString(input: List<String>, expansion: Long = 2): Galaxy {
      val galaxies =
          input.flatMapIndexed { y, row ->
            row.flatMapIndexed { x, tile ->
              if (tile == '#') listOf(Point(x.toLong(), y.toLong())) else listOf()
            }
          }
      return Galaxy(expanded(galaxies, expansion))
    }
  }

  fun distances(): Sequence<Long> = sequence {
    points.pairCombinations().forEach {
      yield(it.first.distance(it.second))
    }
  }
}

fun main() {
  fun part1(input: Galaxy): Long = input.distances().sum()

  fun part2(input: Galaxy): Long = input.distances().sum()

  val testStr =
      """
      ...#......
      .......#..
      #.........
      ..........
      ......#...
      .#........
      .........#
      ..........
      .......#..
      #...#.....
          """
          .trimIndent()
          .lines()
  val inputStr = File("Day11.txt").readLines()

  val test = Galaxy.fromString(testStr)
  val input = Galaxy.fromString(inputStr)

  println(part1(test))
  println(part1(input))

  val test21 = Galaxy.fromString(testStr, 10)
  val test22 = Galaxy.fromString(testStr, 100)
  val input2 = Galaxy.fromString(inputStr, 1_000_000)

  println(part2(test21))
  println(part2(test22))
  println(part2(input2))
}

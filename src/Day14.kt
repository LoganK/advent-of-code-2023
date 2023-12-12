import java.io.File

data class Dish(val room: List<String>) {
  companion object {
    fun fromString(input: List<String>): Dish = Dish(input)
  }

  private fun rotateLeft(count: Int): Dish =
      Dish(
          (1..count).fold(room) { it, _ ->
            it.flatMap(String::withIndex).groupBy({ it.index }, { it.value }).map {
              it.value.joinToString(separator = "")
            }
          })

  private fun tiltWest(reverse: Boolean = false): Dish =
      Dish(
          room.map {
            val byFixedRocks = it.split('#')
            val tilt =
                byFixedRocks.map {
                  // For each fixed rock, pull all the moving rock to the left.
                  it.partition { (it == 'O') != reverse }.run { first + second }
                }
            tilt.joinToString(separator = "#")
          })

  private fun tiltEast(): Dish = tiltWest(true)

  fun tiltSouth(): Dish = rotateLeft(1).tiltEast().rotateLeft(3)

  fun tiltNorth(): Dish = rotateLeft(1).tiltWest().rotateLeft(3)

  fun spin(): Dish = tiltNorth().tiltWest().tiltSouth().tiltEast()

  fun score(): Long =
      room.mapIndexed { i, row -> row.count { it == 'O' } * (room.size - i) }.sum().toLong()

  override fun toString(): String {
    val sb = StringBuilder()
    for (y in room.indices) {
      for (x in room[y].indices) {
        sb.append(room[y][x])
      }
      sb.append('\n')
    }

    return sb.toString()
  }
}

fun main() {
  fun part1(input: Dish): Long = input.tiltNorth().score()

  fun part2(input: Dish): Long {
    // Look for loops
    val histIndex = mutableMapOf<String, Int>()

    var dish = input
    var i = 0
    val limit = 1000000000
    while (i < limit) {
      val loop = histIndex.getOrPut(dish.toString(), { i })
      if (loop != i) {
        i += (i - loop) * ((limit - i - 1) / (i - loop))
      }

      dish = dish.spin()
      i += 1
    }
    return dish.score()
  }

  val testStr =
      """
      O....#....
      O.OO#....#
      .....##...
      OO.#O....O
      .O.....O#.
      O.#..O.#.#
      ..O..#O..O
      .......O..
      #....###..
      #OO..#....
    """
          .trimIndent()
          .lines()
  val inputStr = File("Day14.txt").readLines()

  val test = Dish.fromString(testStr)
  val input = Dish.fromString(inputStr)

  println(part1(test))
  println(part1(input))
  println(part2(test))
  println(part2(input))
}

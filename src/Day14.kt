import java.io.File

data class Dish(val room: MutableList<MutableList<Char>>) {
  companion object {
    fun fromString(input: List<String>): Dish =
        Dish(input.map { it.toMutableList() }.toMutableList())
  }

  fun tiltNorth() {
    for (y in room.indices) {
      val prevRow = room.getOrNull(y - 1)
      val row = room[y]
      for (x in row.indices) {
        if (prevRow?.get(x) == '.' && row[x] == 'O') {
          for (shiftY in (y - 1 downTo 0)) {
            if (room[shiftY][x] == '.') {
              room[shiftY][x] = 'O'
              room[shiftY + 1][x] = '.'
            }
            if (room[shiftY][x] == '#') {
              break
            }
          }
        }
      }
    }
  }

  fun tiltSouth() {
    for (y in room.indices.reversed()) {
      for (x in room[y].indices) {
        if (room.getOrNull(y + 1)?.get(x) == '.' && room[y][x] == 'O') {
          for (shiftY in (y + 1..room.lastIndex)) {
            if (room[shiftY][x] == '.') {
              room[shiftY][x] = 'O'
              room[shiftY - 1][x] = '.'
            }
            if (room[shiftY][x] == '#') {
              break
            }
          }
        }
      }
    }
  }

  fun tiltWest() {
    for (x in room[0].indices) {
      for (y in room.indices) {
        if (room.get(y).getOrNull(x - 1) == '.' && room[y][x] == 'O') {
          for (shiftX in (x - 1 downTo 0)) {
            if (room[y][shiftX] == '.') {
              room[y][shiftX] = 'O'
              room[y][shiftX + 1] = '.'
            }
            if (room[y][shiftX] == '#') {
              break
            }
          }
        }
      }
    }
  }

  fun tiltEast() {
    for (x in room[0].indices.reversed()) {
      for (y in room.indices) {
        if (room.get(y).getOrNull(x + 1) == '.' && room[y][x] == 'O') {
          for (shiftX in (x + 1..room[y].lastIndex)) {
            if (room[y][shiftX] == '.') {
              room[y][shiftX] = 'O'
              room[y][shiftX - 1] = '.'
            }
            if (room[y][shiftX] == '#') {
              break
            }
          }
        }
      }
    }
  }

  fun score(): Long {
    val height = room.size
    return room.mapIndexed { i, row -> row.count { it == 'O' } * (height - i) }.sum().toLong()
  }

  fun copy(): Dish = Dish(room.map { it.toMutableList() }.toMutableList())

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
  fun part1(input: Dish): Long {
    val dish = input.copy()
    dish.tiltNorth()
    return dish.score()
  }

  fun part2(input: Dish): Long {
    val dish = input.copy()
    val cache = mutableMapOf<String, Int>()
    var i = 0
    val limit = 1000000000
    while (i < limit) {
      val str = dish.toString()
      val loop = cache.getOrDefault(str, null)
      if (loop != null) {
        i += (i - loop) * ((limit - i - 1) / (i - loop))
      } else {
        cache[str] = i
      }
      dish.tiltNorth()
      dish.tiltWest()
      dish.tiltSouth()
      dish.tiltEast()
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

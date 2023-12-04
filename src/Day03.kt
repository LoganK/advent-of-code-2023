import java.io.File

data class Point(val x: Int, val y: Int)

data class Num(val ul: Point, val lr: Point, val num: Int) {
  fun near(sym: Sym): Boolean =
      (ul.x - 1..lr.x + 1).contains(sym.loc.x) && (ul.y - 1..lr.y + 1).contains(sym.loc.y)
}

data class Sym(val loc: Point, val c: Char) {
  fun near(num: Num): Boolean = num.near(this)
}

data class Board(val nums: List<Num>, val syms: List<Sym>) {
  companion object {
    private val linePatt = Regex("""(?<num>\d+)|(?<sym>[^\d.])""")

    fun fromString(lines: Collection<String>): Board {
      val nums = mutableListOf<Num>()
      val syms = mutableListOf<Sym>()
      for ((y, line) in lines.withIndex()) {
        for (mg in linePatt.findAll(line)) {
          val start = Point(mg.range.start, y)
          val end = Point(mg.range.endInclusive, y)
          when (val numStr = mg.groups.get("num")?.value) {
            null -> {}
            else -> nums.add(Num(start, end, numStr.toInt()))
          }
          when (val symStr = mg.groups.get("sym")?.value) {
            null -> {}
            else -> syms.add(Sym(start, symStr[0]))
          }
        }
      }
      return Board(nums, syms)
    }
  }
}

fun main() {
  fun part1(board: Board): Int =
      board.nums.filter { num -> board.syms.any { sym -> num.near(sym) } }.sumOf { it.num }
  fun part2(board: Board): Int {
    val stars = board.syms.filter { it.c == '*' }
    val pairs =
        stars.map { sym -> board.nums.filter { num -> num.near(sym) } }.filter { it.size == 2 }
    return pairs.map { it[0].num * it[1].num }.sum()
  }
  val test =
      """
    467..114..
    ...*......
    ..35..633.
    ......#...
    617*......
    .....+.58.
    ..592.....
    ......755.
    ...$.*....
    .664.598..
    """
          .trimIndent()
          .lines()
  val input = File("Day03.txt").readLines()

  val testBoard = Board.fromString(test)
  val inputBoard = Board.fromString(input)

  println(part1(testBoard))
  println(part2(testBoard))
  println(part1(inputBoard))
  println(part2(inputBoard))
}

import java.io.File

data class Point(val x: Int, val y: Int)

data class Graph(val pipes: List<List<Char>>) {
  companion object {
    fun fromString(input: List<String>): Graph {
      return Graph(input.map(String::toList))
    }
  }

  fun findLongest(): Long {
    val visited = mutableSetOf<Point>()
    var visit = findConnecting(findStart())
    var loopDistances = mutableListOf<Long>()

    var distance = 1L
    while (visit.size > 0) {
      val adjacent = mutableListOf<Point>()
      for (p in visit) {
        visited.add(p)
        val connecting = findConnecting(p)
        val newConnecting = connecting - visited
        if (adjacent.intersect(newConnecting).any()) {
          // We've seen this point twice so we have a loop.
          loopDistances.add(distance + 1)
        }
        adjacent.addAll(newConnecting)
      }

      distance++
      visit = adjacent
    }

    return loopDistances.max()
  }

  private fun findStart(): Point =
      pipes
          .flatMapIndexed { y, r ->
            r.flatMapIndexed { x, c -> if (c == 'S') listOf(Point(x, y)) else listOf() }
          }
          .first()

  private fun findConnecting(p: Point): List<Point> {
    val points = mutableListOf<Point>()
    when (val pipe = pipes.getOrNull(p.y)?.getOrNull(p.x - 1)) {
      null -> {}
      else ->
          when (pipe) {
            '-',
            'L',
            'F' -> points.add(Point(p.x - 1, p.y))
            else -> {}
          }
    }
    when (val pipe = pipes.getOrNull(p.y)?.getOrNull(p.x + 1)) {
      null -> {}
      else ->
          when (pipe) {
            '-',
            'J',
            '7' -> points.add(Point(p.x + 1, p.y))
            else -> {}
          }
    }
    when (val pipe = pipes.getOrNull(p.y - 1)?.getOrNull(p.x)) {
      null -> {}
      else ->
          when (pipe) {
            '|',
            'F',
            '7' -> points.add(Point(p.x, p.y - 1))
            else -> {}
          }
    }
    when (val pipe = pipes.getOrNull(p.y + 1)?.getOrNull(p.x)) {
      null -> {}
      else ->
          when (pipe) {
            '|',
            'J',
            'L' -> points.add(Point(p.x, p.y + 1))
            else -> {}
          }
    }

    return points
  }
}

fun main() {
  fun part1(input: Graph): Long = input.findLongest()

  fun part2(input: Graph): Long = 1

  val test1Str =
      """
      .....
      .S-7.
      .|.|.
      .L-J.
      .....
    """.trimIndent().lines()
  val test2Str =
      """
      -L|F7
      7S-7|
      L|7||
      -L-J|
      L|-JF
    """.trimIndent().lines()
  val test3Str =
      """
      ..F7.
      .FJ|.
      SJ.L7
      |F--J
      LJ...
    """.trimIndent().lines()
  val inputStr = File("Day10.txt").readLines()

  val test1 = Graph.fromString(test1Str)
  val test2 = Graph.fromString(test2Str)
  val test3 = Graph.fromString(test3Str)
  val input = Graph.fromString(inputStr)

  println(part1(test1))
  println(part1(test2))
  println(part1(test3))
  println(part1(input))

  val test21Str =
      """
  ...........
  .S-------7.
  .|F-----7|.
  .||.....||.
  .||.....||.
  .|L-7.F-J|.
  .|..|.|..|.
  .L--J.L--J.
  ...........
  """
          .trimIndent()
          .lines()

  val test22Str =
      """
  ..........
  .S------7.
  .|F----7|.
  .||OOOO||.
  .||OOOO||.
  .|L-7F-J|.
  .|II||II|.
  .L--JL--J.
  ..........
  """
          .trimIndent()
          .lines()

  val test23Str =
      """
  .F----7F7F7F7F-7....
  .|F--7||||||||FJ....
  .||.FJ||||||||L7....
  FJL7L7LJLJ||LJ.L-7..
  L--J.L7...LJS7F-7L7.
  ....F-J..F7FJ|L7L7L7
  ....L7.F7||L7|.L7L7|
  .....|FJLJ|FJ|F7|.LJ
  ....FJL-7.||.||||...
  ....L---J.LJ.LJLJ...
  """
          .trimIndent()
          .lines()

  val test24Str =
      """
  FF7FSF7F7F7F7F7F---7
  L|LJ||||||||||||F--J
  FL-7LJLJ||||||LJL-77
  F--JF--7||LJLJ7F7FJ-
  L---JF-JLJ.||-FJLJJ7
  |F|F-JF---7F7-L7L|7|
  |FFJF7L7F-JF7|JL---7
  7-L-JL7||F7|L7F-7F7|
  L.L7LFJ|||||FJL7||LJ
  L7JLJL-JLJLJL--JLJ.L
  """
          .trimIndent()
          .lines()

  val test21 = Graph.fromString(test21Str)
  val test22 = Graph.fromString(test22Str)
  val test23 = Graph.fromString(test23Str)
  val test24 = Graph.fromString(test24Str)
  println(part2(test21))
  println(part2(test22))
  println(part2(test23))
  println(part2(test24))
  println(part2(input))
}

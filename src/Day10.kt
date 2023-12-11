import java.io.File

data class Point(val x: Int, val y: Int)

data class AdjacentPoint(val parent: AdjacentPoint?, val p: Point)

data class Graph(val pipes: List<List<Char>>) {
  companion object {
    fun fromString(input: List<String>): Graph {
      return Graph(input.map(String::toList))
    }
  }

  fun findLongest(): Long = findLongestPath().first

  fun findLongestPath(): Pair<Long, List<Point>> {
    val visited = mutableSetOf<Point>()
    var visit = findConnecting(AdjacentPoint(null, findStart()))
    var biggestLoop: Pair<Long, Pair<AdjacentPoint, AdjacentPoint>>? = null

    var distance = 1L
    while (visit.size > 0) {
      val adjacent = mutableListOf<AdjacentPoint>()
      for (p in visit) {
        visited.add(p.p)
        val newConnected = findConnecting(p).filter { !visited.contains(it.p) }
        for (new in newConnected) {
          // It's a loop if we reach the same point from two different directions.
          // The latest one must be the largest loop because distance only increases.
          when (val existing = adjacent.firstOrNull { it.p == new.p }) {
            null -> adjacent.add(new)
            else -> {
              biggestLoop = Pair(distance + 1, Pair(existing, new))
            }
          }
        }
      }

      distance++
      visit = adjacent
    }

    val path = mutableListOf<Point>()
    var mp: AdjacentPoint? = biggestLoop?.second?.first
    // Build in reverse: midpoint -> start
    while (mp != null) {
      path.add(0, mp.p)
      mp = mp.parent
    }
    mp = biggestLoop?.second?.second?.parent
    while (mp != null) {
      if (mp.parent != null) {
        path.add(mp.p)
      }
      mp = mp.parent
    }
    return Pair(biggestLoop?.first ?: 0, path)
  }

  private fun findStart(): Point =
      pipes
          .flatMapIndexed { y, r ->
            r.flatMapIndexed { x, c -> if (c == 'S') listOf(Point(x, y)) else listOf() }
          }
          .first()

  private fun findConnecting(ap: AdjacentPoint): List<AdjacentPoint> {
    val p = ap.p
    val tile = pipes.get(p.y).get(p.x)
    val points = mutableListOf<AdjacentPoint>()
    if (tile in listOf('S', '-', 'J', '7')) {
      when (val pipe = pipes.getOrNull(p.y)?.getOrNull(p.x - 1)) {
        null -> {}
        else ->
            when (pipe) {
              '-',
              'L',
              'F' -> points.add(AdjacentPoint(ap, Point(p.x - 1, p.y)))
              else -> {}
            }
      }
    }
    if (tile in listOf('S', '-', 'F', 'L')) {
      when (val pipe = pipes.getOrNull(p.y)?.getOrNull(p.x + 1)) {
        null -> {}
        else ->
            when (pipe) {
              '-',
              'J',
              '7' -> points.add(AdjacentPoint(ap, Point(p.x + 1, p.y)))
              else -> {}
            }
      }
    }
    if (tile in listOf('S', '|', 'J', 'L')) {
      when (val pipe = pipes.getOrNull(p.y - 1)?.getOrNull(p.x)) {
        null -> {}
        else ->
            when (pipe) {
              '|',
              'F',
              '7' -> points.add(AdjacentPoint(ap, Point(p.x, p.y - 1)))
              else -> {}
            }
      }
    }
    if (tile in listOf('S', '|', '7', 'F')) {
      when (val pipe = pipes.getOrNull(p.y + 1)?.getOrNull(p.x)) {
        null -> {}
        else ->
            when (pipe) {
              '|',
              'J',
              'L' -> points.add(AdjacentPoint(ap, Point(p.x, p.y + 1)))
              else -> {}
            }
      }
    }

    return points
  }
}

fun main() {
  fun part1(input: Graph): Long = input.findLongest()

  fun part2(input: Graph): Long {
    val path = input.findLongestPath().second
    var containCount = 0L
    for (y in input.pipes.indices) {
      var contained = false
      var lastBend: Char? = null

      for (x in input.pipes.get(y).indices) {
        if (Point(x, y) in path) {
          // Vertical crossings should partition into enclosed ranges. TODO: Look
          // for a real algorithm? Maybe something like flood fill?
          var tile = input.pipes.get(y).get(x)
          // For simpliciy find the "shape" of the start connection.
          if (tile == 'S') {
            var first = path.get(1)
            var last = path.last()
            if (first.x > last.x) {
              first = last.also { last = first }
            }
            tile =
                when (Pair(last.x - first.x, last.y - first.y)) {
                  // Remember that y is top-to-bottom.
                  Pair(0, 2),
                  Pair(0, -2) -> '|'
                  Pair(2, 0),
                  Pair(-2, 0) -> '-'
                  // SL vs  L
                  // F     FS
                  Pair(1, -1) -> if (first.x == x) 'F' else 'J'
                  // F  vs FS
                  // SL     L
                  Pair(1, 1) -> if (first.x == x) 'L' else '7'
                  else -> throw IllegalStateException("Bad start data")
                }
          }
          val cut =
              when (tile) {
                'L' -> {
                  lastBend = tile
                  false
                }
                '7' -> lastBend == 'L'
                'F' -> {
                  lastBend = tile
                  false
                }
                'J' -> lastBend == 'F'
                '|' -> true
                else -> false
              }
          if (cut) {
            contained = !contained
          }
          print(tile)
        } else if (contained) {
          containCount++
          print('I')
        } else {
          print('.')
        }
      }
      println("")
    }

    return containCount
  }

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
  .||....||.
  .||....||.
  .|L-7F-J|.
  .|..||..|.
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

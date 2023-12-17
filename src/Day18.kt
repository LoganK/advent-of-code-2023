package D18

import java.io.File

data class Point(val x: Int, val y: Int) {
  operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
}

data class Edge(val dst: Point, val color: String, val inEdge: Edge? = null) {
  val src: Point
    get() = inEdge?.dst ?: Point(0, 0)

  fun xRange(): IntRange = if (src.x < dst.x) src.x..dst.x else dst.x..src.x

  fun yRange(): IntRange = if (src.y < dst.y) src.y..dst.y else dst.y..src.y
}

data class Trench(val edges: List<Edge>) {
  companion object {
    fun fromString(input: List<String>): Trench {
      var loc = Point(0, 0)
      val edges = mutableListOf<Edge>()
      for (line in input) {
        val (dir, lenStr, col) = line.split(' ')
        val len = lenStr.toInt()
        val newLoc =
            when (dir) {
              "U" -> loc + Point(0, -len)
              "R" -> loc + Point(len, 0)
              "D" -> loc + Point(0, len)
              "L" -> loc + Point(-len, 0)
              else -> throw IllegalStateException("Unknown dir ${dir}")
            }
        if (edges.size > 0) {
          edges.add(Edge(newLoc, col, edges.last()))
        } else {
          edges.add(Edge(newLoc, col))
        }
        loc = newLoc
      }
      return Trench(edges)
    }

    fun fromString2(input: List<String>): Trench {
      var loc = Point(0, 0)
      val edges = mutableListOf<Edge>()
      for (line in input) {
        val (_, _, encoded) = line.split(' ')
        val dir =
            when (encoded[encoded.lastIndex - 1]) {
              '0' -> "R"
              '1' -> "D"
              '2' -> "L"
              '3' -> "U"
              else -> throw IllegalStateException("Unknown encoded dir ${encoded}")
            }
        val len = encoded.substring(2, encoded.lastIndex - 1).toInt(16)
        val newLoc =
            when (dir) {
              "U" -> loc + Point(0, -len)
              "R" -> loc + Point(len, 0)
              "D" -> loc + Point(0, len)
              "L" -> loc + Point(-len, 0)
              else -> throw IllegalStateException("Unknown dir ${dir}")
            }
        if (edges.size > 0) {
          edges.add(Edge(newLoc, encoded, edges.last()))
        } else {
          edges.add(Edge(newLoc, encoded))
        }
        loc = newLoc
      }
      return Trench(edges)
    }
  }

  fun volume(): Long {
    val ys = edges.map { it.src.y }
    val yRange = ys.min()..ys.max()

    var totalVol = 0L
    for (y in yRange) {
      val inRow = edges.filter { y in it.yRange() }.sortedBy { minOf(it.src.x, it.dst.x) }

      var rowTotal = 0L
      var inside = false
      var startX = 0
      var lastCorner = Char.MIN_VALUE // ^ or v
      for (e in inRow) {
        if (e.src.y != y && e.dst.y != y) {
          inside = !inside
          if (!inside) {
            rowTotal += 1L + e.dst.x - startX
          } else {
            startX = e.dst.x
          }
          lastCorner = Char.MIN_VALUE
        } else if (minOf(e.src.y, e.dst.y) < y) {
          when (lastCorner) {
            'v' -> {
              inside = !inside
              if (!inside) {
                rowTotal += 1L + e.dst.x - startX
              }
              lastCorner = Char.MIN_VALUE
            }
            '^' -> {
              lastCorner = Char.MIN_VALUE
              if (!inside) {
                rowTotal += 1L + e.dst.x - startX
              }
            }
            else -> {
              lastCorner = '^'
              if (!inside) {
                startX = e.dst.x
              }
            }
          }
        } else if (maxOf(e.src.y, e.dst.y) > y) {
          when (lastCorner) {
            '^' -> {
              inside = !inside
              if (!inside) {
                rowTotal += 1L + e.dst.x - startX
              }
              lastCorner = Char.MIN_VALUE
            }
            'v' -> {
              lastCorner = Char.MIN_VALUE
              if (!inside) {
                rowTotal += 1L + e.dst.x - startX
              }
            }
            else -> {
              lastCorner = 'v'
              if (!inside) {
                startX = e.dst.x
              }
            }
          }
        }
      }

      totalVol += rowTotal
    }

    return totalVol.toLong()
  }

  override fun toString(): String {
    val sb = StringBuilder()
    val xs = edges.map { it.src.x }
    val xRange = xs.min()..xs.max()
    val ys = edges.map { it.src.y }
    val yRange = ys.min()..ys.max()
    for (y in yRange) {
      val inRow = edges.filter { y in it.yRange() }.sortedBy { minOf(it.src.x, it.dst.x) }
      for (x in xRange) {
        if (inRow.any { x in it.xRange() }) {
          sb.append('#')
        } else {
          sb.append('.')
        }
      }
      sb.append('\n')
    }
    return sb.toString()
  }

  fun toStringFilled(): String {
    val sb = StringBuilder()
    val xs = edges.map { it.src.x }
    val xRange = xs.min()..xs.max()
    val ys = edges.map { it.src.y }
    val yRange = ys.min()..ys.max()
    for (y in yRange) {
      val inRow = edges.filter { y in it.yRange() }.sortedBy { minOf(it.src.x, it.dst.x) }

      val dugX = mutableSetOf<Int>()
      var inside = false
      var lastX = 0
      var lastCorner = Char.MIN_VALUE // ^ or v
      for (e in inRow) {
        dugX.addAll(e.xRange()) // The trench itself
        if (inside) {
          dugX.addAll(lastX..e.src.x) // Infill
        }
        if (e.src.y != y && e.dst.y != y) {
          inside = !inside
          lastX = e.src.x
          lastCorner = Char.MIN_VALUE
        } else if (minOf(e.src.y, e.dst.y) < y) {
          when (lastCorner) {
            'v' -> {
              inside = !inside
              lastX = e.src.x
              lastCorner = Char.MIN_VALUE
            }
            '^' -> lastCorner = Char.MIN_VALUE
            else -> lastCorner = '^'
          }
        } else if (maxOf(e.src.y, e.dst.y) > y) {
          when (lastCorner) {
            '^' -> {
              inside = !inside
              lastX = e.src.x
              lastCorner = Char.MIN_VALUE
            }
            'v' -> lastCorner = Char.MIN_VALUE
            else -> lastCorner = 'v'
          }
        }
      }

      sb.append(xRange.map { if (it in dugX) '#' else '.' }.toCharArray())
      sb.append('\n')
    }
    return sb.toString()
  }

  fun toStringCorners(): String {
    val sb = StringBuilder()
    val xs = edges.map { it.src.x }
    val xRange = xs.min()..xs.max()
    val ys = edges.map { it.src.y }
    val yRange = ys.min()..ys.max()
    for (y in yRange) {
      val inRow = edges.filter { y in it.yRange() }.sortedBy { minOf(it.src.x, it.dst.x) }
      val corners = inRow.map { it.src }.toSet()
      for (x in xRange) {
        if (Point(x, y) in corners || (x == 0 && y == 0)) {
          sb.append('#')
        } else {
          sb.append('.')
        }
      }
      sb.append('\n')
    }
    return sb.toString()
  }
}

fun main() {
  fun part1(input: Trench): Long = input.volume()

  fun part2(input: Trench): Long = input.volume()

  val testStr =
      """
      R 6 (#70c710)
      D 5 (#0dc571)
      L 2 (#5713f0)
      D 2 (#d2c081)
      R 2 (#59c680)
      D 2 (#411b91)
      L 5 (#8ceee2)
      U 2 (#caa173)
      L 1 (#1b58a2)
      U 2 (#caa171)
      R 2 (#7807d2)
      U 3 (#a77fa3)
      L 2 (#015232)
      U 2 (#7a21e3)
    """
          .trimIndent()
          .lines()
  val inputStr = File("Day18.txt").readLines()

  val test = Trench.fromString(testStr)
  val input = Trench.fromString(inputStr)

  // println(test)
  // println("\n")
  // println(input.toStringFilled())
  println(part1(test))
  println(part1(input))

  val test2 = Trench.fromString2(testStr)
  val input2 = Trench.fromString2(inputStr)
  println(part2(test2))
  println(part2(input2))
}

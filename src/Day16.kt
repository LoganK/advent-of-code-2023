package D16

import java.io.File

data class Point(val x: Int, val y: Int)

data class Beam(val p: Point, val dir: Dir) {
  enum class Dir {
    U,
    R,
    D,
    L
  }

  fun next(): Point =
      when (dir) {
        Dir.U -> Point(p.x, p.y - 1)
        Dir.R -> Point(p.x + 1, p.y)
        Dir.D -> Point(p.x, p.y + 1)
        Dir.L -> Point(p.x - 1, p.y)
      }
}

typealias Dir = Beam.Dir

data class Tile(val c: Char, val beams: MutableSet<Beam> = mutableSetOf())

data class Room(
    val layout: List<List<Char>>
) {
  companion object {
    fun fromString(input: List<String>): Room = Room(input.map { it.toList() })
  }

  // Modifies beams and tiles
  private fun tick(beams: MutableList<Beam>, tiles: List<List<Tile>>): Unit {
    val newBeams = mutableListOf<Beam>()
    for (b in beams) {
      val p = b.next()
      val t = tiles.getOrNull(p.y)?.getOrNull(p.x)

      // Keep the full history to avoid loops.
      if (t != null && t.beams.add(b)) {
        when (t.c) {
          '.' -> newBeams.add(b.copy(p = p))
          '-' -> {
            when (b.dir) {
              Dir.L,
              Dir.R -> newBeams.add(b.copy(p = p))
              Dir.U,
              Dir.D -> newBeams.addAll(listOf(b.copy(p = p, Dir.L), b.copy(p = p, Dir.R)))
            }
          }
          '|' -> {
            when (b.dir) {
              Dir.U,
              Dir.D -> newBeams.add(b.copy(p = p))
              Dir.L,
              Dir.R -> newBeams.addAll(listOf(b.copy(p = p, Dir.U), b.copy(p = p, Dir.D)))
            }
          }
          '/' -> {
            when (b.dir) {
              Dir.U -> newBeams.add(b.copy(p = p, Dir.R))
              Dir.R -> newBeams.add(b.copy(p = p, Dir.U))
              Dir.D -> newBeams.add(b.copy(p = p, Dir.L))
              Dir.L -> newBeams.add(b.copy(p = p, Dir.D))
            }
          }
          '\\' ->
              when (b.dir) {
                Dir.U -> newBeams.add(b.copy(p = p, Dir.L))
                Dir.R -> newBeams.add(b.copy(p = p, Dir.D))
                Dir.D -> newBeams.add(b.copy(p = p, Dir.R))
                Dir.L -> newBeams.add(b.copy(p = p, Dir.U))
              }
          else -> throw IllegalStateException("tick: what is ${t}")
        }
      }
    }

    beams.clear()
    beams.addAll(newBeams)
  }

  fun run(beams: MutableList<Beam>): Long {
    val tiles = layout.map { it.map { Tile(it) } }
    while (beams.size > 0) {
      tick(beams, tiles)
    }
    return tiles
      .map { row -> row.filter { it.beams.isNotEmpty() }.count() }
      .sum().toLong()
  }
}

fun main() {
  fun part1(input: Room): Long = input.run(mutableListOf(Beam(Point(-1, 0), Dir.R)))

  fun part2(input: Room): Long {
    val lastCol = input.layout[0].lastIndex
    val starts =
        input.layout.indices.map { Beam(Point(-1, it), Dir.R) } +
            input.layout.indices.map { Beam(Point(lastCol + 1, it), Dir.L) } +
            (0..lastCol).map { Beam(Point(it, -1), Dir.D) } +
            (0..lastCol).map { Beam(Point(it, input.layout.lastIndex + 1), Dir.D) }

    return starts.map { input.run(mutableListOf(it)) }.max()
  }

  val testStr =
      """
      .|...\....
      |.-.\.....
      .....|-...
      ........|.
      ..........
      .........\
      ..../.\\..
      .-.-/..|..
      .|....-|.\
      ..//.|....
    """
          .trimIndent()
          .lines()
  val inputStr = File("Day16.txt").readLines()

  val test = Room.fromString(testStr)
  val input = Room.fromString(inputStr)

  println(part1(test))
  println(part1(input))
  println(part2(test))
  println(part2(input))
}

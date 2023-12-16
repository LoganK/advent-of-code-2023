package D16

import java.io.File

data class Point(val x: Int, val y: Int)

data class Beam(val p: Point, var dir: Dir) {
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
    val layout: List<List<Tile>>,
    var beams: MutableList<Beam> = mutableListOf(Beam(Point(-1, 0), Beam.Dir.R))
) {
  companion object {
    fun fromString(input: List<String>): Room = Room(input.map { it.toList().map { Tile(it) } })
  }

  fun tick(): Unit {
    val newBeams = mutableListOf<Beam>()
    for (b in beams) {
      val p = b.next()
      val t = layout.getOrNull(p.y)?.getOrNull(p.x)

      // Keep the history to avoid loops.
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

    beams = newBeams
  }

  fun run(): Room {
    // Cheesy bad code: reset the map between runs.
    layout.forEach { it.forEach { it.beams.clear() } }
    while (beams.size > 0) {
      tick()
    }
    return this
  }

  fun energy(): Long =
      layout.map { row -> row.filter { it.beams.isNotEmpty() }.count() }.sum().toLong()
}

fun main() {
  fun part1(input: Room): Long = input.run().energy()

  fun part2(input: Room): Long {
    val lastCol = input.layout[0].lastIndex
    var max =
        input.layout.indices
            .map { input.copy(beams = mutableListOf(Beam(Point(-1, it), Dir.R))).run().energy() }
            .max()
    max =
        maxOf(
            max,
            input.layout.indices
                .map {
                  input
                      .copy(beams = mutableListOf(Beam(Point(lastCol + 1, it), Dir.L)))
                      .run()
                      .energy()
                }
                .max())
    max =
        maxOf(
            max,
            (0..lastCol)
                .map {
                  input.copy(beams = mutableListOf(Beam(Point(it, -1), Dir.D))).run().energy()
                }
                .max())
    max =
        maxOf(
            max,
            (0..lastCol)
                .map {
                  input
                      .copy(
                          beams = mutableListOf(Beam(Point(it, input.layout.lastIndex + 1), Dir.U)))
                      .run()
                      .energy()
                }
                .max())
    return max
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

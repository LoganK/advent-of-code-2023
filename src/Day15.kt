import java.io.File

fun String.HASH(): Int = map { it.code }.fold(0) { acc, it -> (acc + it) * 17 % 256 }

data class Lens(val label: String, val focalLength: Int, val op: Char) {
  companion object {
    private val patt = Regex("""(.+)([-=])(\d*)""")

    fun fromString(input: String): Lens {
      val (l, o, f) = patt.matchEntire(input)!!.destructured
      return Lens(l, if (f.isNotEmpty()) f.toInt() else 0, o[0])
    }
  }

  fun HASH(): Int = label.HASH()
}

data class Box(var lenses: List<Lens> = listOf()) {
  fun add(lens: Lens): Box {
    val i = lenses.indexOfFirst { it.label == lens.label }
    if (i >= 0) {
      return Box(lenses.subList(0, i).plus(lens) + lenses.subList(i + 1, lenses.size))
    } else {
      return Box(lenses.plus(lens))
    }
  }

  fun remove(lens: Lens): Box {
    val i = lenses.indexOfFirst { it.label == lens.label }
    if (i >= 0) {
      return Box(lenses.subList(0, i) + lenses.subList(i + 1, lenses.size))
    }

    return this
  }
}

class Hashmap() {
  val boxes = Array<Box>(256) { Box() }

  fun add(lens: Lens): Unit {
    boxes[lens.HASH()] =
        when (lens.op) {
          '=' -> boxes[lens.HASH()].add(lens)
          '-' -> boxes[lens.HASH()].remove(lens)
          else -> throw IllegalStateException("Unknown op")
        }
  }

  fun focusingPower(): Long =
      boxes
          .mapIndexed { bi, box ->
            (box.lenses.mapIndexed { li, lens -> (li + 1) * lens.focalLength }.sum() * (bi + 1))
                .toLong()
          }
          .sum()
}

fun main() {
  fun part1(input: List<String>): Long = input.map(String::HASH).sum().toLong()

  fun part2(input: List<String>): Long {
    val hm = Hashmap()
    input.map { Lens.fromString(it) }.forEach { hm.add(it) }
    return hm.focusingPower()
  }

  val testStr =
      """
      rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7
    """
          .trimIndent()
  val inputStr = File("Day15.txt").readText().trim()

  val test = testStr.split(',')
  val input = inputStr.split(',')

  println(part1(test))
  println(part1(input))
  println(part2(test))
  println(part2(input))
}

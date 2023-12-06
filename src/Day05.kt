import java.io.File
import kotlin.math.max
import kotlin.math.min

data class ShiftedRange(val range: LongRange, val offset: Long) {
  // Assumes a valid mapping.
  fun mapLong(num: Long): Long {
    assert(num in range)
    return num + offset
  }

  // Only returns the overlapping range.
  fun mapRange(newRange: LongRange): LongRange =
      LongRange(
          max(range.start, newRange.start) + offset,
          min(range.endInclusive, newRange.endInclusive) + offset)

  fun overlaps(rhs: LongRange): Boolean =
      rhs.start in range ||
          rhs.endInclusive in range ||
          (range.start >= rhs.start && range.endInclusive <= rhs.endInclusive)
}

class Entry() {
  var mapping = arrayListOf(ShiftedRange(LongRange(0, Long.MAX_VALUE), 0))

  fun extend(input: String) {
    val (dst, src, length) = input.split(' ').map(String::toLong)
    val newRange = LongRange(src, src + length - 1)
    val newOffset = dst - src

    val i = mapping.indexOfFirst { it.range.endInclusive > newRange.start }
    assert(i >= 0)
    val oldRange = mapping[i]
    if (oldRange.range.endInclusive == newRange.endInclusive) {
      mapping[i] = ShiftedRange(newRange, newOffset)
    } else {
      mapping[i] =
          ShiftedRange(
              LongRange(newRange.endInclusive + 1, oldRange.range.endInclusive), oldRange.offset)
      mapping.add(i, ShiftedRange(newRange, newOffset))
    }
    if (oldRange.range.start < newRange.start) {
      mapping.add(
          i, ShiftedRange(LongRange(oldRange.range.start, newRange.start - 1), oldRange.offset))
    }
  }

  fun lookup(num: Long): Long = mapping.first { num in it.range }.let { it.mapLong(num) }

  fun lookup(num: List<LongRange>): List<LongRange> =
      num.flatMap { n -> mapping.filter { it.overlaps(n) }.map { it.mapRange(n) } }
}

data class Almanac(val seeds: List<LongRange>, val maps: Map<Pair<String, String>, Entry>) {
  companion object {
    private val mapPatt = Regex("""([^-]+)-to-([^ ]+) map:""")

    fun fromStringP1(input: List<String>): Almanac {
      assert(input[0].startsWith("seeds: "))
      val seeds =
          input[0].substring("seeds: ".length).split(' ').map(String::toLong).map {
            LongRange(it, it)
          }

      return Almanac(seeds, parseMaps(input.drop(1)))
    }

    fun fromStringP2(input: List<String>): Almanac {
      assert(input[0].startsWith("seeds: "))
      val seeds = input[0].substring("seeds: ".length).split(' ').map(String::toLong)
      val seedRanges = seeds.chunked(2).map { LongRange(it[0], it[0] + it[1] - 1) }

      return Almanac(seedRanges, parseMaps(input.drop(1)))
    }

    private fun parseMaps(input: List<String>): Map<Pair<String, String>, Entry> {
      val maps = mutableMapOf<Pair<String, String>, Entry>()
      var currEntry: Entry? = null
      for (line in input.drop(1)) {
        if (line.isBlank()) {
          currEntry = null
          continue
        }
        val mg = mapPatt.matchEntire(line)
        if (mg != null) {
          val src = mg.groups.get(1)!!.value
          val dst = mg.groups.get(2)!!.value
          currEntry = Entry()
          maps[Pair(src, dst)] = currEntry
          continue
        }
        if (currEntry != null) {
          currEntry.extend(line)
        }
      }

      return maps
    }
  }
}

fun main() {
  fun part1(a: Almanac): Long =
      a.seeds
          .map(LongRange::start)
          .map { a.maps[Pair("seed", "soil")]!!.lookup(it) }
          .map { a.maps[Pair("soil", "fertilizer")]!!.lookup(it) }
          .map { a.maps[Pair("fertilizer", "water")]!!.lookup(it) }
          .map { a.maps[Pair("water", "light")]!!.lookup(it) }
          .map { a.maps[Pair("light", "temperature")]!!.lookup(it) }
          .map { a.maps[Pair("temperature", "humidity")]!!.lookup(it) }
          .map { a.maps[Pair("humidity", "location")]!!.lookup(it) }
          .min()

  fun part2(a: Almanac): Long =
      a.maps[Pair("seed", "soil")]!!
          .lookup(a.seeds)
          .let { a.maps[Pair("soil", "fertilizer")]!!.lookup(it) }
          .let { a.maps[Pair("fertilizer", "water")]!!.lookup(it) }
          .let { a.maps[Pair("water", "light")]!!.lookup(it) }
          .let { a.maps[Pair("light", "temperature")]!!.lookup(it) }
          .let { a.maps[Pair("temperature", "humidity")]!!.lookup(it) }
          .let { a.maps[Pair("humidity", "location")]!!.lookup(it) }
          .minOf { it.start }

  val testStr =
      """
      seeds: 79 14 55 13

      seed-to-soil map:
      50 98 2
      52 50 48
      
      soil-to-fertilizer map:
      0 15 37
      37 52 2
      39 0 15
      
      fertilizer-to-water map:
      49 53 8
      0 11 42
      42 0 7
      57 7 4
      
      water-to-light map:
      88 18 7
      18 25 70
      
      light-to-temperature map:
      45 77 23
      81 45 19
      68 64 13
      
      temperature-to-humidity map:
      0 69 1
      1 0 69
      
      humidity-to-location map:
      60 56 37
      56 93 4
         """
          .trimIndent()
          .lines()
  val inputStr = File("Day05.txt").readLines()

  val test = Almanac.fromStringP1(testStr)
  val input = Almanac.fromStringP1(inputStr)

  println(part1(test))
  println(part1(input))

  val testP2 = Almanac.fromStringP2(testStr)
  val inputP2 = Almanac.fromStringP2(inputStr)
  println(part2(testP2))
  println(part2(inputP2))
}

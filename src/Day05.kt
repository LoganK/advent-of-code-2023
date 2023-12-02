import java.io.File
import kotlin.math.max
import kotlin.math.min

data class ShiftedRange(val range: ULongRange, val base: ULong) {
  // Assumes a valid mapping.
  fun mapULong(num: ULong): ULong = base - range.start + num

  // Only returns the overlapping range.
  fun mapRange(newRange: ULongRange): ULongRange =
      ULongRange(
          base - range.start + max(range.start, newRange.start),
          base - range.start + min(range.endInclusive, newRange.endInclusive))

  fun overlaps(rhs: ULongRange): Boolean =
      rhs.start in range ||
          rhs.endInclusive in range ||
          (range.start >= rhs.start && range.endInclusive <= rhs.endInclusive)
}

class Entry() {
  var mapping = arrayListOf(ShiftedRange(ULongRange(0u, ULong.MAX_VALUE), 0u))

  fun extend(input: String) {
    val (dst, src, length) = input.split(' ').map(String::toULong)
    val srcRange = ULongRange(src, src + length - 1u)

    val i = mapping.indexOfFirst { it.range.endInclusive > srcRange.start }
    assert(i >= 0)
    val oldRange = mapping[i]
    if (oldRange.range.endInclusive == srcRange.endInclusive) {
      mapping[i] = ShiftedRange(srcRange, dst)
    } else {
      mapping[i] =
          ShiftedRange(
              ULongRange(srcRange.endInclusive + 1u, oldRange.range.endInclusive),
              oldRange.base + srcRange.endInclusive + 1u - oldRange.range.start)
      mapping.add(i, ShiftedRange(srcRange, dst))
    }
    if (oldRange.range.start < srcRange.start) {
      mapping.add(
          i, ShiftedRange(ULongRange(oldRange.range.start, srcRange.start - 1u), oldRange.base))
    }
  }

  fun lookup(num: ULong): ULong = mapping.first { num in it.range }.let { it.mapULong(num) }

  fun lookup(num: List<ULongRange>): List<ULongRange> =
      num.flatMap { n -> mapping.filter { it.overlaps(n) }.map { it.mapRange(n) } }
}

data class Almanac(val seeds: List<ULongRange>, val maps: Map<Pair<String, String>, Entry>) {
  companion object {
    private val mapPatt = Regex("""([^-]+)-to-([^ ]+) map:""")

    fun fromStringP1(input: List<String>): Almanac {
      assert(input[0].startsWith("seeds: "))
      val seeds =
          input[0].substring("seeds: ".length).split(' ').map(String::toULong).map {
            ULongRange(it, it)
          }

      return Almanac(seeds, parseMaps(input.drop(1)))
    }

    fun fromStringP2(input: List<String>): Almanac {
      assert(input[0].startsWith("seeds: "))
      val seeds = input[0].substring("seeds: ".length).split(' ').map(String::toULong)
      val sStart = seeds.filterIndexed { i, _ -> i % 2 == 0 }
      val sLength = seeds.filterIndexed { i, _ -> i % 2 == 1 }
      val seedRanges = sStart.zip(sLength).map { ULongRange(it.first, it.first + it.second - 1u) }

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
  fun part1(a: Almanac): ULong =
      a.seeds
          .map(ULongRange::start)
          .map { a.maps[Pair("seed", "soil")]!!.lookup(it) }
          .map { a.maps[Pair("soil", "fertilizer")]!!.lookup(it) }
          .map { a.maps[Pair("fertilizer", "water")]!!.lookup(it) }
          .map { a.maps[Pair("water", "light")]!!.lookup(it) }
          .map { a.maps[Pair("light", "temperature")]!!.lookup(it) }
          .map { a.maps[Pair("temperature", "humidity")]!!.lookup(it) }
          .map { a.maps[Pair("humidity", "location")]!!.lookup(it) }
          .min()

  fun part2(a: Almanac): ULong =
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

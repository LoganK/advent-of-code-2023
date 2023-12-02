import java.io.File
import kotlin.math.max
import kotlin.math.min

class Entry() {
  val map: MutableMap<ULongRange, ULongRange> = mutableMapOf()

  fun extend(input: String) {
    val nums = input.split(' ')
    val dst = nums[0].toULong()
    val src = nums[1].toULong()
    val length = nums[2].toULong()

    map[ULongRange(src, src + length - 1u)] = ULongRange(dst, dst + length - 1u)
  }

  fun lookup(num: ULong): ULong {
    val e = map.entries.firstOrNull { num in it.key }
    if (e != null) {
      return e.value.start + e.key.indexOf(num).toULong()
    }

    return num
  }
}

data class Almanac(val seeds: List<ULong>, val maps: Map<Pair<String, String>, Entry>) {
  companion object {
    private val mapPatt = Regex("""([^-]+)-to-([^ ]+) map:""")

    fun fromString(input: List<String>): Almanac {
      assert(input[0].startsWith("seeds: "))
      val seeds = input[0].substring("seeds: ".length).split(' ').map(String::toULong)

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

      return Almanac(seeds, maps)
    }
  }
}

class Entry2() {
  var mapping: ArrayList<Pair<ULongRange, ULong>> =
      arrayListOf(Pair(ULongRange(0u, ULong.MAX_VALUE), 0u))

  fun extend(input: String) {
    val nums = input.split(' ')
    val dst = nums[0].toULong()
    val src = nums[1].toULong()
    val length = nums[2].toULong()
    val srcRange = ULongRange(src, src + length - 1u)

    val i = mapping.indexOfFirst { it.first.endInclusive > srcRange.start }
    assert(i >= 0)
    val oldRange = mapping[i]
    if (oldRange.first.endInclusive == srcRange.endInclusive) {
      mapping[i] = Pair(srcRange, dst)
    } else {
      mapping[i] =
          Pair(
              ULongRange(srcRange.endExclusive, oldRange.first.endInclusive),
              oldRange.second + srcRange.endExclusive - oldRange.first.start)
      mapping.add(i, Pair(srcRange, dst))
    }
    if (oldRange.first.start < srcRange.start) {
      mapping.add(i, Pair(ULongRange(oldRange.first.start, srcRange.start - 1u), oldRange.second))
    }
  }

  private fun mapToRange(region: Pair<ULongRange, ULong>, range: ULongRange): ULongRange =
      ULongRange(
          region.second + (max(range.start, region.first.start) - region.first.start),
          region.second + (min(range.endInclusive, region.first.endInclusive) - region.first.start))

  fun lookup(num: List<ULongRange>): List<ULongRange> {
    fun ULongRange.isSubset(rhs: ULongRange): Boolean =
        rhs.start in this ||
            rhs.endInclusive in this ||
            (start >= rhs.start && endInclusive <= rhs.endInclusive)

    val res = mutableListOf<ULongRange>()
    for (n in num) {
      val subset = mapping.filter { it.first.isSubset(n) }
      // println("Matched: ${n} -> ${subset}")
      res.addAll(subset.map { mapToRange(it, n) })
    }
    return res
  }
}

data class Almanac2(val seeds: List<ULongRange>, val maps: Map<Pair<String, String>, Entry2>) {
  companion object {
    private val mapPatt = Regex("""([^-]+)-to-([^ ]+) map:""")

    fun fromString(input: List<String>): Almanac2 {
      assert(input[0].startsWith("seeds: "))
      val seeds = input[0].substring("seeds: ".length).split(' ').map(String::toULong)
      val sStart = seeds.filterIndexed { i, n -> i % 2 == 0 }
      val sLength = seeds.filterIndexed { i, n -> i % 2 == 1 }
      val seedRanges = sStart.zip(sLength).map { ULongRange(it.first, it.first + it.second - 1u) }

      val maps = mutableMapOf<Pair<String, String>, Entry2>()
      var currEntry: Entry2? = null
      for (line in input.drop(1)) {
        if (line.isBlank()) {
          currEntry = null
          continue
        }
        val mg = mapPatt.matchEntire(line)
        if (mg != null) {
          val src = mg.groups.get(1)!!.value
          val dst = mg.groups.get(2)!!.value
          currEntry = Entry2()
          maps[Pair(src, dst)] = currEntry
          continue
        }
        if (currEntry != null) {
          currEntry.extend(line)
        }
      }

      return Almanac2(seedRanges, maps)
    }
  }
}

fun main() {
  fun part1(a: Almanac): ULong =
      a.seeds
          .map { a.maps[Pair("seed", "soil")]!!.lookup(it) }
          .map { a.maps[Pair("soil", "fertilizer")]!!.lookup(it) }
          .map { a.maps[Pair("fertilizer", "water")]!!.lookup(it) }
          .map { a.maps[Pair("water", "light")]!!.lookup(it) }
          .map { a.maps[Pair("light", "temperature")]!!.lookup(it) }
          .map { a.maps[Pair("temperature", "humidity")]!!.lookup(it) }
          .map { a.maps[Pair("humidity", "location")]!!.lookup(it) }
          .min()

  fun part2(a: Almanac2): ULong =
      a.maps[Pair("seed", "soil")]!!
          .lookup(a.seeds)
          // .also(::println)
          .let { a.maps[Pair("soil", "fertilizer")]!!.lookup(it) }
          // .also(::println)
          .let { a.maps[Pair("fertilizer", "water")]!!.lookup(it) }
          // .also(::println)
          .let { a.maps[Pair("water", "light")]!!.lookup(it) }
          // .also(::println)
          .let { a.maps[Pair("light", "temperature")]!!.lookup(it) }
          // .also(::println)
          .let { a.maps[Pair("temperature", "humidity")]!!.lookup(it) }
          // .also(::println)
          .let { a.maps[Pair("humidity", "location")]!!.lookup(it) }
          // .also(::println)
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

  val test = Almanac.fromString(testStr)
  val input = Almanac.fromString(inputStr)

  println(part1(test))
  println(part1(input))

  val test2 = Almanac2.fromString(testStr)
  val input2 = Almanac2.fromString(inputStr)
  println(part2(test2))
  println(part2(input2))
}

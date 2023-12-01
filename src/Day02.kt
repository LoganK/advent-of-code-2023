import java.io.File;

data class CubeSet(val cubes: Map<String, Int>) {
    companion object {
        fun fromString(input: String): CubeSet =
            CubeSet(input
                .split(", ")
                .map { it.split(" ") }
                .map { it[1] to it[0].toInt() }
                .toMap())
    }

    fun isSubset(arg: CubeSet): Boolean =
        arg.cubes.all { cubes[it.key] ?: 0 >= it.value }

    fun minBag(rhs: CubeSet): CubeSet =
        CubeSet(cubes.keys.union(rhs.cubes.keys)
                .map { it to maxOf(cubes.getOrDefault(it, 0), rhs.cubes.getOrDefault(it, 0)) }.toMap())
}
data class Game(val id: Int, val sets: List<CubeSet>) {
    companion object {
        fun fromString(input: String): Game {
            val match = Regex("""Game (\d+): (.*)""").matchEntire(input)!!
            val id = match.groups[1]!!.value.toInt()
            return Game(id, match.groups[2]!!.value.split("; ").map(CubeSet::fromString))
        }
    }

    fun minBag(): CubeSet =
        sets.reduce { acc, p -> acc.minBag(p) }
}

fun main() {
    fun part1(input: List<String>): Int {
        val claim = CubeSet.fromString("12 red, 13 green, 14 blue")
        return input
            .map { Game.fromString(it) }
            .filter { it.sets.all { claim.isSubset(it) } }
            .sumOf { it.id }
    }

    fun part2(input: List<String>): Int {
        return input
            .map { Game.fromString(it).minBag() }
            .map { it.cubes.values.reduce { acc, c -> acc * c } }
            .sum()
    }

    val testLines = """
Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green""".trimIndent().lines()
    println(part1(testLines))
    println(part2(testLines))

    val input = File("src/Day02.txt").readLines()
    println(part1(input))
    println(part2(input))
 
}

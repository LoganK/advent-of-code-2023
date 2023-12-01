import java.io.File;

data class Pick(val red: Int, val green: Int, val blue: Int) {
    companion object {
        fun fromString(input: String): Pick {
            val picks = input
                .split(", ")
                .map { it.split(" ") }
                .map { it[1] to it[0].toInt() }
                .toMap()
            return Pick(
                picks.get("red") ?: 0,
                picks.get("green") ?: 0,
                picks.get("blue") ?: 0)
        }
    }
}
fun String.toPicks(): List<Pick> =
    split("; ")
        .map { Pick.fromString(it) }
data class Game(val id: Int, val picks: List<Pick>) {
    companion object {
        fun fromString(input: String): Game {
            val match = Regex("""Game (\d+): (.*)""").matchEntire(input)!!
            val id = match.groups[1]!!.value.toInt()
            return Game(id, match.groups[2]!!.value.toPicks())
        }
    }

    fun minBag(): Pick =
        picks.reduce { acc, p -> Pick(maxOf(acc.red, p.red), maxOf(acc.green, p.green), maxOf(acc.blue, p.blue)) }
}
data class Claim(val red: Int, val green: Int, val blue: Int) {
    fun valid(pick: Pick): Boolean {
        return pick.red <= red && pick.green <= green && pick.blue <= blue
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val claim = Claim(12, 13, 14)
        return input
            .map { Game.fromString(it) }
            .filter { it.picks.all { claim.valid(it) } }
            .sumOf { it.id }
    }

    fun part2(input: List<String>): Int {
        return input
            .map { Game.fromString(it).minBag() }
            .map { it.red * it.green * it.blue }
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

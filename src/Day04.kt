import java.io.File

data class Card(val num: Int, val winNums: Set<Int>, val pickNums: Set<Int>) {
  companion object {
    private val cardPatt = Regex("""Card\s+(\d+):\s+([^|]+)\s+\|\s+(.*)""")

    fun fromString(input: String): Card {
      val (numStr, winStr, pickStr) = cardPatt.matchEntire(input)!!.destructured
      val num = numStr.toInt()
      val wins = winStr.split(' ').filter(String::isNotBlank).map(String::toInt).toSet()
      val picks = pickStr.split(' ').filter(String::isNotBlank).map(String::toInt).toSet()

      return Card(num, wins, picks)
    }
  }

  val wins: Int
    get() { return winNums.intersect(pickNums).size }

  val score: Int
    get() { return if (wins > 0) 1.shl(wins - 1) else 0 }
}

fun main() {
  fun part1(cards: List<Card>): Int = cards.map(Card::score).sum()

  fun part2(cards: List<Card>): Int {
    val cardCount = MutableList<Int>(cards.size) { 1 }
    cards.forEachIndexed { i, card -> (i + 1..i + card.wins).forEach { cardCount[it] += cardCount[i] } }
    return cardCount.sum()
  }

  val testStr =
      """
Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
    """
          .trimIndent()
          .lines()
  val inputStr = File("Day04.txt").readLines()

  val test = testStr.map(Card::fromString)
  val input = inputStr.map(Card::fromString)

  println(part1(test))
  println(part1(input))
  println(part2(test))
  println(part2(input))
}

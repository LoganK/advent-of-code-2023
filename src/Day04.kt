import java.io.File

data class Card(val num: Int, val wins: Set<Int>, val picks: Set<Int>) {
  companion object {
    fun fromString(input: String): Card {
      val first = input.split(':')
      val second = first[1].split('|')

      val num = first[0].substring(5).trim().toInt()
      val wins = second[0].split(' ').filter { it != "" }.map { it.toInt() }.toSet()
      val picks = second[1].split(' ').filter { it != "" }.map { it.toInt() }.toSet()

      return Card(num, wins, picks)
    }
  }

  fun wins(): Int = wins.intersect(picks).size

  fun score(): Int {
    val winCount = wins()
    return if (winCount > 0) 1.shl(winCount - 1) else 0
  }
}

fun main() {
  fun part1(cards: List<Card>): Int = cards.map(Card::score).sum()

  fun part2(cards: List<Card>): Int {
    val cardCount = MutableList<Int>(cards.size) { 1 }
    cards.forEachIndexed { i, card ->
      val wins = card.wins()
      (i + 1..i + wins).forEach { cardCount[it] += cardCount[i] }
    }
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

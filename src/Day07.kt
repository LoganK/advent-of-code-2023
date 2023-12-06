import java.io.File

data class Hand(val cards: List<Char>, val jokers: Boolean = false) : Comparable<Hand> {
  companion object {
    val rankMap = listOf('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A')
    val rankMap2 = listOf('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A')

    fun fromString(input: String): Hand {
      return Hand(input.toList())
    }
  }

  fun rank(): Int {
    // 10 - High card
    // 20 - Pair
    // 25 - Two pair
    // 30 - Three of a kind
    // 35 - Full house
    // 40 - Four of a kind
    // 50 - Five of a kind
    val m = cards.groupingBy { it }.eachCount()
    val max = m.values.max()
    return when (max) {
      1 -> 10
      2 -> if (m.values.filter { it == 2 }.count() == 2) 25 else 20
      3 -> if (2 in m.values) 35 else 30
      4 -> 40
      5 -> 50
      else -> throw IllegalArgumentException("Invalid: ${cards}")
    }
  }

  fun rank2(): Int {
    val m = cards.filter { it != 'J' }.groupingBy { it }.eachCount()

    // 10 - High card
    // 20 - Pair
    // 25 - Two pair
    // 30 - Three of a kind
    // 35 - Full house
    // 40 - Four of a kind
    // 50 - Five of a kind
    val max = m.values.maxOrNull() ?: 0
    val jokers = cards.filter { it == 'J' }.count()
    return when (max) {
      0 -> 50 // All Jokers
      1 ->
          when (jokers) {
            0 -> 10
            1 -> 20
            2 -> 30
            3 -> 40
            4 -> 50
            else -> throw IllegalArgumentException("Invalid: ${cards}")
          }
      2 ->
          if (m.values.filter { it == 2 }.count() == 2) {
            if (jokers > 0) 35 else 25
          } else {
            when (jokers) {
              0 -> 20
              1 -> 30
              2 -> 40
              3 -> 50
              else -> throw IllegalArgumentException("Invalid: ${cards}")
            }
          }
      3 ->
          when (jokers) {
            0 -> if (2 in m.values) 35 else 30
            1 -> 40
            2 -> 50
            else -> throw IllegalArgumentException("Invalid: ${cards}")
          }
      4 ->
          when (jokers) {
            0 -> 40
            1 -> 50
            else -> throw IllegalArgumentException("Invalid: ${cards}")
          }
      5 -> 50
      else -> throw IllegalArgumentException("Invalid: ${cards}")
    }
  }

  fun compareTo2(other: Hand): Int {
    val diff = this.rank2() - other.rank2()
    if (diff != 0) {
      return diff
    }
    for (i in (0 ..< cards.size)) {
      if (cards[i] != other.cards[i]) {
        return rankMap2.indexOf(cards[i]) - rankMap2.indexOf(other.cards[i])
      }
    }
    return 0
  }

  override fun compareTo(other: Hand): Int {
    if (jokers) {
      return compareTo2(other)
    }

    val diff = this.rank() - other.rank()
    if (diff != 0) {
      return diff
    }
    for (i in (0 ..< cards.size)) {
      if (cards[i] != other.cards[i]) {
        return rankMap.indexOf(cards[i]) - rankMap.indexOf(other.cards[i])
      }
    }
    return 0
  }
}

data class Bid(val hand: Hand, val bid: ULong) : Comparable<Bid> {
  companion object {
    fun fromString(input: String): Bid {
      val (hStr, bStr) = input.split(' ')
      return Bid(Hand.fromString(hStr), bStr.toULong())
    }
  }

  override fun compareTo(other: Bid): Int {
    return hand.compareTo(other.hand)
  }
}

fun main() {
  fun part1(cards: List<Bid>): ULong =
      cards.sorted().mapIndexed { i, b -> (i + 1).toULong() * b.bid }.reduce { acc, i -> acc + i }

  fun part2(cards: List<Bid>): ULong =
      cards
          .map { Bid(it.hand.copy(jokers = true), it.bid) }
          .sorted()
          .mapIndexed { i, b -> (i + 1).toULong() * b.bid }
          .reduce { acc, i -> acc + i }

  val testStr =
      """
      32T3K 765
      T55J5 684
      KK677 28
      KTJJT 220
      QQQJA 483
          """
          .trimIndent()
          .lines()
  val inputStr = File("Day07.txt").readLines()

  val test = testStr.map(Bid::fromString)
  val input = inputStr.map(Bid::fromString)

  println(part1(test))
  println(part1(input))

  println(part2(test))
  println(part2(input))
}

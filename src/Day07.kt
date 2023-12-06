import java.io.File
import java.math.BigInteger

data class Hand(val cards: List<Char>, val jokers: Boolean = false) : Comparable<Hand> {
  companion object {
    val rankMap = listOf('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A')
    val rankMap2 = listOf('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A')

    fun fromString(input: String): Hand {
      return Hand(input.toList())
    }
  }

  fun rank(): Int {
    val m = mutableMapOf<Char, Int>()
    for (c in cards) {
      m[c] = m.getOrDefault(c, 0) + 1
    }

    val max = m.values.max()
    val fh = max == 3 && 2 in m.values
    val tp = max == 2 && m.values.filter { it == 2 }.count() == 2
    return when (max) {
      1 -> 1
      2 -> if (tp) 3 else 2
      3 -> if (fh) 5 else 4
      4 -> 6
      5 -> 7
      else -> 10
    }
  }

  fun rank2(): Int {
    val m = mutableMapOf<Char, Int>()
    for (c in cards) {
      if (c != 'J') {
        m[c] = m.getOrDefault(c, 0) + 1
      }
    }

    val max = if (m.values.size == 0) 0 else m.values.max()
    val jokers = cards.filter { it == 'J' }.count()
    val fh = max == 3 && 2 in m.values
    val tp = max == 2 && m.values.filter { it == 2 }.count() == 2
    return when (max) {
      0 -> 7
      1 ->
          when (jokers) {
            0 -> 1
            1 -> 2
            2 -> 4
            3 -> 6
            4 -> 7
            else -> throw IllegalArgumentException("Invalid: ${cards}")
          }
      2 ->
          if (tp) {
            if (jokers > 0) 5 else 3
          } else {
            when (jokers) {
              0 -> 2
              1 -> 4
              2 -> 6
              3 -> 7
              else -> throw IllegalArgumentException("Invalid: ${cards}")
            }
          }
      3 ->
          if (fh) {
            5
          } else {
            when (jokers) {
              0 -> 4
              1 -> 6
              2 -> 7
              else -> throw IllegalArgumentException("Invalid: ${cards}")
            }
          }
      4 -> if (jokers > 0) 7 else 6
      5 -> 7
      else -> throw IllegalArgumentException("Invalid: ${cards}")
    }
  }

  override fun compareTo(other: Hand): Int {
    if (jokers) {
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
  fun part1(cards: List<Bid>): BigInteger =
      cards
          .sorted()
          .mapIndexed { i, b -> ((i + 1).toULong() * b.bid).toLong().toBigInteger() }
          .reduce { acc, i -> acc + i }

  fun part2(cards: List<Bid>): BigInteger =
      cards
          .map { Bid(it.hand.copy(jokers = true), it.bid) }
          .sorted()
          .mapIndexed { i, b -> ((i + 1).toULong() * b.bid).toLong().toBigInteger() }
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

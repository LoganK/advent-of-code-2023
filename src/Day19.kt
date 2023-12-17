package D19

import java.io.File

data class Part(val scores: Map<String, Int>) {
  companion object {
    fun fromString(input: String): Part =
        Part(
            input
                .trim('{', '}')
                .split(',')
                .map { it.split('=').let { s -> s[0] to s[1].toInt() } }
                .toMap())
  }

  fun score(): Long = scores.values.sum().toLong()
}

data class PartCat(val scores: Map<String, IntRange>) {
  fun combinations(): Long =
      scores.values.map { 1L + it.endInclusive - it.start }.reduce { acc, it -> acc * it }
}

data class Rule(val field: String?, val op: Char?, val limit: Int?, val next: String) {
  companion object {
    private val rulePatt = Regex("""((?<name>\w+)(?<op>[<>])(?<val>\d+):)?(?<next>\w+)""")

    fun fromString(input: String): Rule {
      val g = rulePatt.matchEntire(input)!!.groups
      return Rule(
          g.get("name")?.value,
          g.get("op")?.value?.get(0),
          g.get("val")?.value?.toInt(),
          g.get("next")!!.value)
    }
  }

  fun eval(m: Map<String, Int>): String? =
      when (op) {
        '<' -> if (m.get(field)?.compareTo(limit!!) ?: 0 < 0) next else null
        '>' -> if (m.get(field)?.compareTo(limit!!) ?: 0 > 0) next else null
        else -> next
      }

  // Returns a workflow name, the PartCat to apply to that workflow, and the
  // PartCat that passes through evaluation.
  fun eval(m: PartCat): Triple<String?, PartCat?, PartCat?> =
      if (op == null) {
        Triple(next, m, null)
      } else {
        val s = m.scores
        val oldRange = s.get(field)!!
        val (nextRange, remRange) =
            when (op) {
              '<' ->
                  Pair(
                      (oldRange.start..minOf(oldRange.endInclusive, limit!! - 1)),
                      (limit..oldRange.endInclusive))
              '>' ->
                  Pair(
                      (maxOf(oldRange.start, limit!! + 1)..oldRange.endInclusive),
                      (oldRange.start..limit))
              else -> throw IllegalStateException("unknown op ${op}")
            }

        val nextCat = if (nextRange.isEmpty()) null else PartCat(s.plus(field!! to nextRange))
        val remCat = if (remRange.isEmpty()) null else PartCat(s.plus(field!! to remRange))
        if (nextCat != null) Triple(next, nextCat, remCat) else Triple(null, null, m)
      }
}

data class Rules(val rules: Sequence<Rule>) {
  companion object {
    fun fromString(input: String): Rules =
        Rules(input.split(',').map(Rule::fromString).asSequence())
  }

  fun eval(m: Map<String, Int>): String? = rules.firstNotNullOfOrNull { it.eval(m) }

  fun eval(m: PartCat): List<Pair<String, PartCat>> {
    var paths = mutableListOf<Pair<String, PartCat>>()
    var remainingM = m
    for (r in rules) {
      val (next, nextCat, remCat) = r.eval(remainingM)

      if (nextCat != null) {
        paths.add(Pair(next!!, nextCat))
      }

      if (remCat == null) {
        break
      }
      remainingM = remCat
    }

    return paths
  }
}

data class System(val workflow: Map<String, Rules>, val parts: List<Part>) {
  companion object {
    fun fromString(input: String): System {
      val (workflowStr, partStr) = input.split("\n\n")

      val workflow = mutableMapOf<String, Rules>()
      for (l in workflowStr.lines()) {
        val (name, ruleStr, _) = l.split('{', '}')
        workflow[name] = Rules.fromString(ruleStr)
      }

      val parts = partStr.lines().map(Part::fromString)

      return System(workflow, parts)
    }
  }

  fun eval(part: Part): String {
    var curr = "in"
    while (curr != "R" && curr != "A") {
      curr = workflow[curr]!!.eval(part.scores)!!
    }
    return curr
  }

  fun acceptableCount(p: PartCat): Long {
    var pcs = listOf(Pair("in", p))
    val acceptable = mutableListOf<PartCat>()
    while (pcs.isNotEmpty()) {
      val (good, next) =
          pcs.flatMap { workflow[it.first]!!.eval(it.second) }
              .filter { it.first != "R" }
              .partition { it.first == "A" }
      acceptable.addAll(good.map { it.second })
      pcs = next
    }

    return acceptable.map(PartCat::combinations).sum()
  }
}

fun main() {
  fun part1(input: System): Long =
      input.parts.filter { input.eval(it) == "A" }.map(Part::score).sum()

  fun part2(input: System): Long {
    val p =
        PartCat(
            mapOf(
                "x" to (1..4000),
                "m" to (1..4000),
                "a" to (1..4000),
                "s" to (1..4000),
            ))
    return input.acceptableCount(p)
  }

  val testStr =
      """
      px{a<2006:qkq,m>2090:A,rfg}
      pv{a>1716:R,A}
      lnx{m>1548:A,A}
      rfg{s<537:gd,x>2440:R,A}
      qs{s>3448:A,lnx}
      qkq{x<1416:A,crn}
      crn{x>2662:A,R}
      in{s<1351:px,qqz}
      qqz{s>2770:qs,m<1801:hdj,R}
      gd{a>3333:R,R}
      hdj{m>838:A,pv}
      
      {x=787,m=2655,a=1222,s=2876}
      {x=1679,m=44,a=2067,s=496}
      {x=2036,m=264,a=79,s=2244}
      {x=2461,m=1339,a=466,s=291}
      {x=2127,m=1623,a=2188,s=1013}
    """
          .trimIndent()
  val inputStr = File("Day19.txt").readText().trim()

  val test = System.fromString(testStr)
  val input = System.fromString(inputStr)

  println(part1(test))
  println(part1(input))
  println(part2(test))
  println(part2(input))
}

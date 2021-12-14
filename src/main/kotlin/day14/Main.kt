package day14

import java.io.File

typealias PairCounts = Map<String, Long>

data class Input(val initial: PairCounts, val last: String, var rules: Map<String, String>) {
  companion object {
    fun parse(file: String): Input {
      val lines = File(file).readLines().toMutableList()
      val template = lines.removeFirst()
      while (lines.first().isEmpty())
        lines.removeFirst()

      val re = Regex("""\A(..) -> (.)\z""")
      val rules = mutableMapOf<String, String>()
      lines.forEach { line ->
        val (pair, insert) = re.matchEntire(line)!!.destructured
        rules[pair] = insert
      }

      val pairCounts = template.windowed(2).groupingBy { it }.eachCount().
        map { (k, v) -> k to v.toLong() }.toMap()
      return Input(pairCounts, template.last().toString(), rules)
    }
  }

  fun incr(m: MutableMap<String, Long>, s: String, add: Long = 1) {
    m.compute(s) { _, c -> (c ?: 0) + add }
  }

  fun step(pairs: PairCounts): PairCounts {
    val newPairs = mutableMapOf<String, Long>()
    for ((pair, count) in pairs) {
      when (val insert = rules[pair]) {
        null -> incr(newPairs, pair, count)
        else -> {
          incr(newPairs, pair.take(1) + insert, count)
          incr(newPairs, insert + pair[1], count)
        }
      }
    }
    return newPairs
  }

  fun mostMinusLeast(pairs: PairCounts): Long {
    val counts = mutableMapOf<String, Long>()
    for ((k, v) in pairs) {
      incr(counts, k.first().toString(), v)
    }
    incr(counts, last)
    return counts.values.maxOrNull()!! - counts.values.minOrNull()!!
  }

  fun answerAfterSteps(steps: Int): Long {
    var p = initial
    repeat(steps) {
      p = step(p)
    }
    return mostMinusLeast(p)
  }
}

fun main(args: Array<String>) {
  val input = Input.parse(args.first())
  println(input.answerAfterSteps(10))
  println(input.answerAfterSteps(40))
}

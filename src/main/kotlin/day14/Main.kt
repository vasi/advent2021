package day14

import java.io.File

data class Input(val template: String, var rules: Map<String, String>) {
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
      return Input(template, rules)
    }
  }

  fun step(s: String): String {
    return s.windowed(2).map { pair ->
      if (rules.containsKey(pair))
        pair[0] + rules[pair]!!
      else pair[0]
    }.joinToString("") + s.last()
  }

  fun part1(): Int {
    var s = template
    repeat(10) { s = step(s) }
    val counts = s.groupingBy { it }.eachCount().values
    return counts.maxOrNull()!! - counts.minOrNull()!!
  }
}

fun main(args: Array<String>) {
  val input = Input.parse(args.first())
  println(input.part1())
}

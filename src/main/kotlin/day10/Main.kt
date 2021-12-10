package day10

import java.io.File

data class ParseResults(
  val firstIllegal: Char?,
  val closingChars: List<Char>,
)

fun parse(line: String): ParseResults {
  val pairs = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
  val stack = mutableListOf<Char>()
  for (c in line) {
    if (pairs.containsKey(c)) {
      stack.add(pairs[c]!!)
    } else if (!stack.isEmpty() && stack.last() == c) {
      stack.removeLast()
    } else {
      return ParseResults(c, stack.reversed())
    }
  }
  return ParseResults(null, stack.reversed())
}

fun part1(lines: List<String>): Int {
  val firstIllegals = lines.mapNotNull { parse(it).firstIllegal }
  val scores = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)
  return firstIllegals.map { scores[it]!! }.sum()
}

fun scoreClosingChars(chars: List<Char>): Long {
  val scores = mapOf(')' to 1, ']' to 2, '}' to 3, '>' to 4)
  var score = 0L
  for (c in chars) {
    score = score * 5 + scores[c]!!
  }
  return score
}

fun part2(lines: List<String>): Long {
  val scores = lines.map { parse(it) }
    .filter { it.firstIllegal == null }
    .map { scoreClosingChars(it.closingChars) }
    .sorted()
  return scores[scores.size / 2]
}

fun main(args: Array<String>) {
  val lines = File(args.first()).readLines()
  println(part1(lines))
  println(part2(lines))
}

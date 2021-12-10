package day10

import java.io.File

fun firstIllegal(line: String): Char? {
  val pairs = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
  val stack = mutableListOf<Char>()
  for (c in line) {
    if (pairs.containsKey(c)) {
      stack.add(pairs[c]!!)
    } else if (!stack.isEmpty() && stack.last() == c) {
      stack.removeLast()
    } else {
      return c
    }
  }
  return null
}

fun part1(lines: List<String>): Int {
    val firstIllegals = lines.mapNotNull { firstIllegal(it) }
  val scores = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)
  return firstIllegals.map { scores[it]!! }.sum()
}

fun main(args: Array<String>) {
  val lines = File(args.first()).readLines()
  println(part1(lines))
}

package day3

import java.io.File

fun mostCommonBit(numbers: List<String>, pos: Int): Char? {
  val digits = numbers.map { it[pos] }
  val ones = digits.count { it == '1' }
  if (ones * 2 == digits.size)
    return null
  else if (ones * 2 > digits.size)
    return '1'
  else return '0'
}

fun invertBit(bit: Char): Char {
  return if (bit == '1') '0' else '1'
}

fun joinBits(bits: List<Char>): Int {
  return bits.joinToString("").let { Integer.parseInt(it, 2) }
}

fun part1(numbers: List<String>): Int {
  val width = numbers.first().length
  val gammaBits = (0 until width).map { mostCommonBit(numbers, it)!! }
  val epsilonBits = gammaBits.map { invertBit(it) }
  return joinBits(gammaBits) * joinBits(epsilonBits)
}

fun findRating(numbers: List<String>, mostCommon: Boolean, pos: Int): String {
  if (numbers.size == 1)
    return numbers.first()

  var want = mostCommonBit(numbers, pos) ?: '1'
  if (!mostCommon)
    want = invertBit(want)

  val filtered = numbers.filter { it[pos] == want }
  return findRating(filtered, mostCommon, pos + 1)
}

fun part2(numbers: List<String>): Int {
  val o2gen = findRating(numbers, true, 0)
  val co2scrub = findRating(numbers, false, 0)
  return listOf(o2gen, co2scrub)
    .map { Integer.parseInt(it, 2) }
    .reduce { a, b -> a * b }
}

fun main(args: Array<String>) {
  val lines = File(args.first()).readLines()
  println(part1(lines))
  println(part2(lines))
}

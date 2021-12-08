package day8

import java.io.File

data class Entry(val digits: List<Int>, val output: List<Int>) {
  companion object {
    fun parseDigit(s: String): Int {
      return s.map { 1.shl(it - 'a') }.reduce { a, b -> a.or(b) }
    }

    fun parseEntry(line: String): Entry {
      val (ds, os) = line.split(" | ").
        map { s -> s.split(" ").map { parseDigit(it) } }
      return Entry(ds, os)
    }

    fun parse(file: String): List<Entry> {
      return File(file).readLines().map { parseEntry(it) }
    }

    fun popcnt(i: Int): Int {
      return Integer.bitCount(i)
    }

    fun isPart1Digit(i: Int): Boolean {
      val p = popcnt(i)
      return (p == 2) || (p == 3) || (p == 4) || (p == 7)
    }

    fun part1(entries: List<Entry>): Int {
      return entries.map { es -> es.output.filter { isPart1Digit(it) }.size }.sum()
    }

    fun part2(entries: List<Entry>): Int {
      return entries.map { it.solve() }.sum()
    }
  }

  fun digitWithSegments(c: Int): Int {
    return digits.find { popcnt(it) == c }!!
  }

  fun segmentUsage(seg: Int): Int {
    return digits.count { it.and(seg) != 0 }
  }

  fun segmentWithUsage(candidates: Int, usages: Int): Int {
    var c = candidates
    while (true) {
      val seg = Integer.highestOneBit(c)
      if (segmentUsage(seg) == usages)
        return seg

      c = c.and(seg.inv())
    }
  }

  data class Masks(var top: Int = 0, var topLeft: Int = 0, var topRight: Int = 0, var middle: Int = 0,
    var botLeft: Int = 0, var botRight: Int = 0, var bottom: Int = 0) {

    fun lookup(): Map<Int, Int> {
      return listOf(
        top + topLeft + topRight + botLeft + botRight + bottom,
        topRight + botRight,
        top + topRight + middle + botLeft + bottom,
        top + topRight + middle + botRight + bottom,
        topLeft + topRight + middle + botRight,
        top + topLeft + middle + botRight + bottom,
        top + topLeft + middle + botLeft + botRight + bottom,
        top + topRight + botRight,
        top + topLeft + topRight + middle + botLeft + botRight + bottom,
        top + topLeft + topRight + middle + botRight + bottom,
      ).mapIndexed { i, m -> m to i }.toMap()
    }

    fun decode(digits: List<Int>): Int {
      val lookup = lookup()
      var n = 0
      for (d in digits) {
        n *= 10
        n += lookup[d]!!
      }
      return n
    }
  }

  // Return masks for each segment
  fun solveMasks(): Masks {
    val dOne = digitWithSegments(2)
    val dSeven = digitWithSegments(3)
    val dFour = digitWithSegments(4)
    val dEight = digitWithSegments(7)

    val m = Masks()
    m.top = dSeven - dOne
    m.topLeft = segmentWithUsage(dFour - dOne, 6)
    m.middle = dFour - dOne - m.topLeft
    m.topRight = segmentWithUsage(dOne, 8)
    m.botRight = dOne - m.topRight
    val remain = dEight - m.top - m.topLeft - m.middle - m.topRight - m.botRight
    m.botLeft = segmentWithUsage(remain, 4)
    m.bottom = remain - m.botLeft
    return m
  }

  fun solve(): Int {
    return solveMasks().decode(output)
  }
}

fun main(args: Array<String>) {
  val entries = Entry.parse(args.first())
  println(Entry.part1(entries))
  println(Entry.part2(entries))
}

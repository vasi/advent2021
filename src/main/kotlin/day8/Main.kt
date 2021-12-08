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
  }
}

fun main(args: Array<String>) {
  val entries = Entry.parse(args.first())
  println(Entry.part1(entries))
}

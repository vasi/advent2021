package day4

import java.io.File
import java.lang.RuntimeException

// rows (top-down) of columns (left-right)
class Board(val numbers: List<List<Int>>) {
  val marked = numbers.map { r -> r.map { false }.toMutableList() }

  fun isWon(): Boolean {
    marked.forEach { r ->
      if (r.all { it }) return true
    }
    marked.indices.forEach { i ->
      if (marked.map { it[i] }.all { it }) return true
    }
    return false
  }

  fun mark(chosen: Int) {
    numbers.forEachIndexed { j, r ->
      r.forEachIndexed { i, n ->
        if (n == chosen) marked[j][i] = true
      }
    }
  }

  fun unmarked(): List<Int> {
    return numbers.flatMapIndexed { j, r ->
      r.filterIndexed { i, _ -> !marked[j][i] }
    }
  }
}

data class Input(val draw: List<Int>, val boards: List<Board>) {
  fun part1(): Int {
    for (d in draw) {
      for (b in boards) {
        b.mark(d)
        if (b.isWon())
          return d * b.unmarked().sum()
      }
    }
    throw RuntimeException("Should not get here")
  }
}

fun parse(file: String): Input {
  File(file).inputStream().bufferedReader().use { rd ->
    val draw = rd.readLine().split(",").map { Integer.parseInt(it) }
    val boards = mutableListOf<Board>()
    val rows = mutableListOf<List<Int>>()

    while (true) {
      val row = rd.readLine()
      if (row == null || row == "") {
        if (!rows.isEmpty()) {
          boards.add(Board(rows.toList()))
          rows.clear()
        }
        if (row == null)
          break
      } else {
        val nums = Regex("\\d+").findAll(row).map { Integer.parseInt(it.value) }
        rows.add(nums.toList())
      }
    }

    return Input(draw, boards)
  }
}

fun main(args: Array<String>) {
  val input = parse(args.first())
  println(input.part1())
}

package day15

import java.io.File
import java.util.*

data class Pos(val x: Int, val y: Int) {
  fun adjacent(w: Int, h: Int): List<Pos> {
    val r = mutableListOf<Pos>()
    if (x > 0)
      r.add(Pos(x-1, y))
    if (y > 0)
      r.add(Pos(x, y-1))
    if (x < w - 1)
      r.add(Pos(x+1, y))
    if (y < h - 1)
      r.add(Pos(x, y+1))
    return r
  }
}

data class WeightedPos(val pos: Pos, val weight: Long) : Comparable<WeightedPos> {
  override fun compareTo(other: WeightedPos): Int {
    return weight.compareTo(other.weight)
  }
}

data class Board(val cells: List<List<Int>>) {
  fun height(): Int {
    return cells.size
  }
  fun width(): Int {
    return cells.first().size
  }

  fun at(p: Pos): Int {
    return cells[p.y][p.x]
  }

  companion object {
    fun parse(file: String): Board {
      val heights = File(file).readLines().map { l ->
        l.map { it - '0' }
      }
      return Board(heights)
    }
  }

  fun part1(): Long {
    val complete = mutableSetOf<Pos>()
    val todo = PriorityQueue<WeightedPos>()
    val target = Pos(width() - 1, height() - 1)
    todo.add(WeightedPos(Pos(0, 0), 0))

    while (true) {
      val p = todo.remove()

      if (complete.contains(p.pos))
        continue
      complete.add(p.pos)

      if (p.pos == target)
        return p.weight

      for (a in p.pos.adjacent(width(), height())) {
        todo.add(WeightedPos(a, p.weight + at(a)))
      }
    }
  }
}


fun main(args: Array<String>) {
  val board = Board.parse(args.first())
  println(board.part1())
}

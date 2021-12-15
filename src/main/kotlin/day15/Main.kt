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

interface Board {
  fun height(): Int
  fun width(): Int
  fun at(p: Pos): Int
}

data class SimpleBoard(val cells: List<List<Int>>) : Board {
  override fun height(): Int {
    return cells.size
  }
  override fun width(): Int {
    return cells.first().size
  }
  override fun at(p: Pos): Int {
    return cells[p.y][p.x]
  }

  companion object {
    fun parse(file: String): Board {
      val heights = File(file).readLines().map { l ->
        l.map { it - '0' }
      }
      return SimpleBoard(heights)
    }
  }
}

data class RepeatingBoard(val base: Board, val repeat: Int) : Board {
  override fun height(): Int {
    return base.height() * repeat
  }

  override fun width(): Int {
    return base.width() * repeat
  }

  override fun at(p: Pos): Int {
    val p2 = Pos(p.x % base.width(), p.y % base.height())
    val b = base.at(p2)
    val b2 = b + (p.x / base.width()) + (p.y / base.height())
    return (b2 - 1) % 9 + 1
  }
}

fun totalRisk(board: Board): Long {
  val complete = mutableSetOf<Pos>()
  val todo = PriorityQueue<WeightedPos>()
  val target = Pos(board.width() - 1, board.height() - 1)
  todo.add(WeightedPos(Pos(0, 0), 0))

  while (true) {
    val p = todo.remove()

    if (complete.contains(p.pos))
      continue
    complete.add(p.pos)

    if (p.pos == target)
      return p.weight

    for (a in p.pos.adjacent(board.width(), board.height())) {
      todo.add(WeightedPos(a, p.weight + board.at(a)))
    }
  }
}


fun main(args: Array<String>) {
  val simple = SimpleBoard.parse(args.first())
  println(totalRisk(simple))
  println(totalRisk(RepeatingBoard(simple, 5)))
}

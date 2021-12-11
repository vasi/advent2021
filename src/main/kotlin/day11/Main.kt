package day11

import java.io.File

data class Pos(val x: Int, val y: Int) {
  companion object {
    val dirs = listOf(-1, 0, 1)
  }

  fun adjacent(w: Int, h: Int): List<Pos> {
    val r = mutableListOf<Pos>()
    dirs.forEach { dx ->
      dirs.forEach { dy ->
        if (dx != 0 || dy != 0) {
          val x2 = x + dx
          if (x2 >= 0 && x2 < w) {
            val y2 = y + dy
            if (y2 >= 0 && y2 < h) {
              r.add(Pos(x2, y2))
            }
          }
        }
      }
    }
    return r
  }
}

data class Board(val cells: List<MutableList<Int>>) {
  companion object {
    fun parse(file: String): Board {
      return Board(File(file).readLines().map { l -> l.map { it - '0' }.toMutableList() })
    }
  }

  fun width(): Int {
    return cells.first().size
  }

  fun height(): Int {
    return cells.size
  }

  fun forall(f: (Pos) -> Unit) {
    for (x in 0..(width()-1)) {
      for (y in 0 .. (height()-1)) {
        f(Pos(x, y))
      }
    }
  }

  fun get(p: Pos): Int {
    return cells[p.y][p.x]
  }

  fun set(p: Pos, i: Int) {
    cells[p.y][p.x] = i
  }

  fun incr(p: Pos) {
    set(p, get(p) + 1)
  }

  fun resetFlash(p: Pos) {
    if (get(p) > 9) {
      set(p, 0)
    }
  }

  fun step(): Long {
    // 1. bump all cells by one
    forall { incr(it) }

    // 2. handle flashes
    var flashes = 0L
    val flashed = mutableSetOf<Pos>()
    val todo = mutableListOf<Pos>()
    forall {
      if (get(it) > 9) {
        todo.add(it)
        flashed.add(it)
      }
    }
    while (!todo.isEmpty()) {
      val p = todo.removeLast()
      flashes += 1
      for (a in p.adjacent(width(), height())) {
        if (!flashed.contains(a)) {
          incr(a)
          if (get(a) > 9) {
            todo.add(a)
            flashed.add(a)
          }
        }
      }
    }

    // 3. reset flashed cells
    forall { resetFlash(it) }

    return flashes
  }

  fun part1(): Long {
    var flashes = 0L
//    print()
    repeat(100) {
      flashes += step()
//      print()
//      println("flashes: $flashes")
    }
    return flashes
  }

  fun print() {
    cells.forEach { r ->
      println(r.joinToString(""))
    }
    println()
  }
}

fun main(args: Array<String>) {
  val board = Board.parse(args.first())
  println(board.part1())
}


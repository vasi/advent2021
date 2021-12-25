package day25

import java.io.File

data class Pos(val x: Int, val y: Int)

data class Grid(val cells: List<MutableList<Char>>) {
  val allPos = (0 until height()).flatMap { y ->
    (0 until width()).map { Pos(it, y) }
  }

  companion object {
    fun parse(file: String): Grid {
      val cells = File(file).readLines().map { l -> l.toCharArray().toMutableList() }
      return Grid(cells)
    }
  }

  override fun toString(): String {
    return cells.map { l -> l.joinToString("") }.joinToString("\n")
  }

  fun at(p: Pos): Char {
    return cells[p.y][p.x]
  }

  fun set(p: Pos, c: Char) {
    cells[p.y][p.x] = c
  }

  fun width(): Int {
    return cells.first().size
  }

  fun height(): Int {
    return cells.size
  }

  fun next(p: Pos, dir: Char): Pos {
    if (dir == '>') {
      return Pos((p.x + 1) % width(), p.y)
    } else {
      return Pos(p.x, (p.y + 1) % height())
    }
  }

  fun stepDir(dir: Char): Boolean {
    val toMove = allPos.filter { p ->
      at(p) == dir && at(next(p, dir)) == '.'
    }
    for (p in toMove) {
      set(next(p, dir), dir)
      set(p, '.')
    }
    return toMove.isNotEmpty()
  }

  fun step(): Boolean {
    val east = stepDir('>')
    val south = stepDir('v')
    return east || south
  }

  fun part1(): Int {
    var steps = 1
    while (step()) {
      steps += 1
    }
    return steps
  }
}

fun main(args: Array<String>) {
  val grid = Grid.parse(args.first())
  println(grid.part1())
}

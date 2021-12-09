package day9

import java.io.File

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

data class Heightmap(val heights: List<List<Int>>) {
  fun height(): Int {
    return heights.size
  }
  fun width(): Int {
    return heights.first().size
  }

  fun at(p: Pos): Int {
    return heights[p.y][p.x]
  }

  companion object {
    fun parse(file: String): Heightmap {
      val heights = File(file).readLines().map { l ->
        l.map { it - '0' }
      }
      return Heightmap(heights)
    }
  }

  fun isLowPoint(p: Pos): Boolean {
    val v = at(p)
    return p.adjacent(width(), height()).all { at(it) > v }
  }

  fun lowPoints(): List<Pos> {
    val h = height()
    val w = width()
    val r = mutableListOf<Pos>()
    for (x in 0..w-1) {
      for (y in 0 .. h-1) {
        val p = Pos(x, y)
        if (isLowPoint(p))
          r.add(p)
      }
    }
    return r
  }

  fun part1(): Int {
    return lowPoints().map { at(it) + 1 }.sum()
  }
}

fun main(args: Array<String>) {
  val heightmap = Heightmap.parse(args.first())
  println(heightmap.part1())
}

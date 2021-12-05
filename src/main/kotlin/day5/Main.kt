package day5

import java.io.File
import kotlin.math.sign

data class Point(val x: Int, val y: Int)

data class Line(val a: Point, val b: Point) {
  fun isDiagonal(): Boolean = a.x != b.x && a.y != b.y

  fun includedPoints(): Sequence<Point> {
    return generateSequence(a) {
      if (it.x == b.x && it.y == b.y)
        null
      else
        Point(
          it.x + (b.x - it.x).sign,
          it.y + (b.y - it.y).sign,
        )
    }
  }
}

data class Input(val lines: List<Line>) {
  companion object {
    fun parse(file: String): Input {
      val re = Regex("""\A(\d+),(\d+) -> (\d+),(\d+)\z""")
      val lines = File(file).readLines().map { line ->
        val (ax, ay, bx, by) = re.matchEntire(line)!!.destructured.toList().map { Integer.parseInt(it) }
        Line(Point(ax, ay), Point(bx, by))
      }
      return Input(lines)
    }
  }

  fun part1(): Int {
    val counts = mutableMapOf<Point, Int>()
    for (line in lines) {
      if (line.isDiagonal())
        continue
      for (p in line.includedPoints()) {
        counts.compute(p) { _, v -> (v ?: 0) + 1}
      }
    }

    return counts.count { (_, v) -> v > 1 }
  }
}

fun main(args: Array<String>) {
  val input = Input.parse(args.first())
  println(input.part1())
}

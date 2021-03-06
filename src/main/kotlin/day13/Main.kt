package day13

import java.io.File

data class Pos(val x: Int, val y: Int)
data class Fold(val dir: String, val line: Int)

data class Input(val dots: Set<Pos>, val folds: List<Fold>) {
  private val width = dots.maxOf { it.x } + 1
  private val height = dots.maxOf { it.y } + 1

  companion object {
    fun parse(file: String): Input {
      val dotRe = Regex("""\A(\d+),(\d+)\z""")
      val foldRe = Regex("""\Afold along (.)=(\d+)\z""")

      val dots = mutableSetOf<Pos>()
      val folds = mutableListOf<Fold>()

      for (line in File(file).readLines()) {
        val dotMatch = dotRe.matchEntire(line)
        if (dotMatch != null) {
          val (x, y) = dotMatch.destructured
          dots.add(Pos(x.toInt(), y.toInt()))
          continue
        }

        val foldMatch = foldRe.matchEntire(line)
        if (foldMatch != null) {
          val (dir, fline) = foldMatch.destructured
          folds.add(Fold(dir, fline.toInt()))
          continue
        }
      }

      return Input(dots, folds)
    }
  }

  fun print() {
    val byY = dots.groupBy { it.y }
    for (y in 0 until height) {
      val xs = byY.getOrDefault(y, listOf()).map { it.x }.toSet()
      for (x in 0 until width) {
        if (xs.contains(x)) print("#")
        else print(".")
      }
      print("\n")
    }
  }

  fun fold(): Input {
    val f = folds.first()
    val ds = dots.map { d ->
      if (f.dir == "x" && d.x > f.line) {
        Pos(f.line - (d.x - f.line), d.y)
      } else if (f.dir == "y" && d.y > f.line) {
        Pos(d.x, f.line - (d.y - f.line))
      } else {
        d
      }
    }.toSet()
    return Input(ds, folds.drop(1))
  }

  fun part1(): Int {
    return fold().dots.size
  }

  fun part2() {
    var i = this
    while (i.folds.isNotEmpty()) {
      i = i.fold()
    }
    i.print()
  }
}

fun main(args: Array<String>) {
  val input = Input.parse(args.first())
  println(input.part1())
  input.part2()
}

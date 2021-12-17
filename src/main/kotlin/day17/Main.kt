package day17

fun yseq(yvel: Int): Sequence<Int> {
  return sequence {
    var y = 0
    var dy = yvel
    while (true) {
      y += dy
      dy -= 1
      yield(y)
    }
  }
}

fun yintersects(dy: Int, ymin: Int, ymax: Int): Boolean {
  return yseq(dy).takeWhile { it >= ymin }.any { it <= ymax }
}

fun ymax(dy: Int): Int {
  return yseq(dy).zipWithNext().takeWhile { (a, b) -> a <= b }.last().second
}

fun part1(ymin: Int, ymax: Int): Int {
  return (1..1000).filter { yintersects(it, ymin, ymax) }.maxOf { ymax(it) }
}

fun main(args: Array<String>) {
  println(part1(-110, -69))
}

package day6

import java.io.File

fun nextGen(fish: List<Int>): List<Int> {
  return fish.flatMap {
    if (it == 0) listOf(6, 8)
    else listOf(it - 1)
  }
}

fun simulate(fish: List<Int>, generations: Int): List<Int> {
  var fs = fish
  repeat(generations) {
    fs = nextGen(fs)
  }
  return fs
}

fun main(args: Array<String>) {
  val fish = File(args.first()).readLines().first().split(",")
    .map { Integer.parseInt(it) }
  println(simulate(fish, 80).size)
}

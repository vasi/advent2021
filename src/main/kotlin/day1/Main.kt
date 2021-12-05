package day1

import java.io.File

fun countIncreases(list: List<Int>): Int {
  return list.windowed(2).count { (a, b) -> b > a }
}

fun main(args: Array<String>) {
  val depths = File(args.first()).readLines().map { Integer.parseInt(it) }
  println(countIncreases(depths))
  println(countIncreases(depths.windowed(3).map { it.sum() }))
}

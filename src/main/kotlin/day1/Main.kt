package day1

import java.io.File

fun main(args: Array<String>) {
  val depths = File(args.first()).readLines().map { Integer.parseInt(it) }
  println(depths.windowed(2).count { (a, b) -> b > a })
}

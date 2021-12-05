package day3

import java.io.File
import java.math.BigInteger

fun main(args: Array<String>) {
  val lines = File(args.first()).readLines()

  val width = lines.first().length
  val gamma = (0 until width).map { d ->
    val digits = lines.map { it[d] }
    val ones = digits.count { it == '1' }
    if (ones * 2 > digits.size) "1" else "0"
  }.joinToString("").let { Integer.parseInt(it, 2) }
  val epsilon = gamma.inv().and(1.shl(width) - 1)

  println(gamma * epsilon)
}

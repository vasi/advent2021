package day7

import java.io.File
import kotlin.math.absoluteValue

fun median(list: List<Int>): Int {
  return list.sorted().get(list.size / 2)
}

fun part1(positions: List<Int>): Int {
  val med = median(positions)
  val cost = positions.map { (it - med).absoluteValue }.sum()
  return cost
}

fun main(args: Array<String>) {
  val positions = File(args.first()).readLines().first()
    .split(",").map { Integer.parseInt(it) }

  println(part1(positions))
}

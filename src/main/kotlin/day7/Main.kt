package day7

import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

fun median(list: List<Int>): Int {
  return list.sorted().get(list.size / 2)
}

fun part1(positions: List<Int>): Int {
  val med = median(positions)
  val cost = positions.map { (it - med).absoluteValue }.sum()
  return cost
}

fun cost2(positions: List<Int>, pos: Int): Int {
  return positions.map {
    val dist = (it - pos).absoluteValue
    dist * (dist + 1) / 2
  }.sum()
}

fun part2(positions: List<Int>): Int {
  // Feels like it should be mean, but maybe I have to check both sides of it? Anyhow this is fast enough
  val min = positions.minOrNull()!!
  val max = positions.maxOrNull()!!
  val pos = (min..max).minByOrNull { cost2(positions, it) }!!
  return cost2(positions, pos)
}

fun main(args: Array<String>) {
  val positions = File(args.first()).readLines().first()
    .split(",").map { Integer.parseInt(it) }

  println(part1(positions))
  println(part2(positions))
}

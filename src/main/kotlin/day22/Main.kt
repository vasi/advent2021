package day22

import java.io.File

data class Instruction(val cuboid: List<IntRange>, val on: Boolean) {
  companion object {
    fun parse(file: String): List<Instruction> {
      return File(file).readLines().map { line ->
        val (dir, coords) = line.split(" ")
        val on = dir == "on"
        val dims = coords.split(",").map {
          val (p1, p2) = Regex(""".=(-?\d+)..(-?\d+)""").matchEntire(it)!!.destructured
          p1.toInt()..p2.toInt()
        }
        Instruction(dims, on)
      }
    }
  }
}

class Grid(val insts: List<Instruction>, val bounding: List<IntRange>? = null) {
  val dims = makeDims()

  fun makeDim(dim: Int): List<IntRange> {
    // start of a new range
    val cutpoints = insts.map { it.cuboid[dim] }.flatMap {
      listOf(it.start, it.endInclusive + 1)
    }.sorted().toMutableList()

    // add the bound
    if (bounding != null) {
      val db = bounding[dim]
      if (cutpoints.removeIf { it < db.start }) {
        cutpoints.add(0, db.start)
      }
      if (cutpoints.removeIf { it >= db.endInclusive }) {
        cutpoints.add(db.endInclusive + 1)
      }
    }

    val r = cutpoints.windowed(2).filter { (a, b) -> a != b }.
      map { (a, b) -> IntRange(a, b - 1) }
    return r
  }

  fun makeDims(): List<List<IntRange>> {
    return (0..2).map { makeDim(it) }
  }

  fun size(rs: List<IntRange>): Long {
    return rs.map { (it.endInclusive - it.start + 1).toLong() }.reduce { a, b -> a * b }
  }

  fun instContains(inst: Instruction, x: IntRange, y: IntRange, z: IntRange): Boolean {
    return inst.cuboid[0].contains(x.start)
        && inst.cuboid[1].contains(y.start)
        && inst.cuboid[2].contains(z.start)
  }

  fun cubesOn(): Long {
    var on = 0L

    for (xd in dims[0]) {
      println("xrange: $xd, on: $on")
      for (yd in dims[1]) {
        for (zd in dims[2]) {
          val inst = insts.findLast { instContains(it, xd, yd, zd) }
          if (inst?.on == true) {
            on += size(listOf(xd, yd, zd))
          }
        }
      }
    }
    return on
  }
}

fun main(args: Array<String>) {
  val insts = Instruction.parse(args.first())
  println(Grid(insts, listOf(-50..50, -50..50, -50..50)).cubesOn())
  println(Grid(insts).cubesOn())
}

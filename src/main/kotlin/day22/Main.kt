package day22

import java.io.File

data class Pos(val dim: List<Int>)

data class Cuboid(val p1: Pos, val p2: Pos) {
  fun size(): Long {
    return (0..2).map { (p2.dim[it] - p1.dim[it] + 1).toLong() }.reduce { a, b -> a * b }
  }

  companion object {
    fun fromRanges(vararg dims: IntRange): Cuboid {
      return Cuboid(Pos(dims.map { it.start }), Pos(dims.map { it.endInclusive }))
    }
  }
}

data class Instruction(val cuboid: Cuboid, val on: Boolean) {
  companion object {
    fun parse(file: String): List<Instruction> {
      return File(file).readLines().map { line ->
        val (dir, coords) = line.split(" ")
        val on = dir == "on"
        val dims = coords.split(",").map {
          val (p1, p2) = Regex(""".=(-?\d+)..(-?\d+)""").matchEntire(it)!!.destructured
          Pair(p1.toInt(), p2.toInt())
        }
        Instruction(Cuboid(
          Pos(dims.map { it.first }),
          Pos(dims.map { it.second }),
        ), on)
      }
    }
  }
}

class Grid(val insts: List<Instruction>, val bounding: Cuboid? = null) {
  val dims = makeDims()
  val on = mutableSetOf<Cuboid>()

  fun makeDim(dim: Int): List<IntRange> {
    // start of a new range
    val cutpoints = insts.map { it.cuboid }.flatMap {
      listOf(it.p1.dim[dim], it.p2.dim[dim] + 1)
    }.sorted().toMutableList()

    // add the bound
    if (bounding != null && cutpoints.removeIf { it < bounding.p1.dim[dim] }) {
      cutpoints.add(0, bounding.p1.dim[dim])
    }
    if (bounding != null && cutpoints.removeIf { it >= bounding.p2.dim[dim] }) {
      cutpoints.add(bounding.p2.dim[dim] + 1)
    }

    val r = cutpoints.windowed(2).filter { (a, b) -> a != b }.
      map { (a, b) -> IntRange(a, b - 1) }
    println(r.size)
    return r
  }

  fun makeDims(): List<List<IntRange>> {
    return (0..2).map { makeDim(it) }
  }

  fun matching(dim: Int, inst: Instruction): List<IntRange> {
    return dims[dim].filter {
      inst.
    }
  }

  fun doInstruction(inst: Instruction) {
  }

  fun cubesOn(): Long {
    return on.map { it.size() }.sum()
  }

  fun doAll(insts: List<Instruction>): Long {
    for (inst in insts) {
      doInstruction(inst)
    }
    return cubesOn()
  }
}

fun main(args: Array<String>) {
  val insts = Instruction.parse(args.first())

  val part1 = Grid(insts, Cuboid.fromRanges(-50..50, -50..50, -50..50))
  println(part1.doAll(insts))

  println(Grid(insts).doAll(insts))
}

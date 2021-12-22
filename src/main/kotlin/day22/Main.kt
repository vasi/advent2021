package day22

import java.io.File

data class Cuboid(val dims: List<IntRange>) {
  fun size(): Long {
    return dims.map { (it.endInclusive - it.start + 1).toLong() }.reduce { a, b -> a * b }
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
          p1.toInt()..p2.toInt()
        }
        Instruction(Cuboid(dims), on)
      }
    }
  }
}

class Grid(val insts: List<Instruction>, val bounding: Cuboid? = null) {
  val dims = makeDims()
  val on = HashSet<Int>()

  fun makeDim(dim: Int): List<IntRange> {
    // start of a new range
    val cutpoints = insts.map { it.cuboid.dims[dim] }.flatMap {
      listOf(it.start, it.endInclusive + 1)
    }.sorted().toMutableList()

    // add the bound
    if (bounding != null) {
      val bound = bounding.dims[dim]
      if (cutpoints.removeIf { it < bound.start }) {
        cutpoints.add(0, bound.start)
      }
      if (cutpoints.removeIf { it >= bound.endInclusive }) {
        cutpoints.add(bound.endInclusive + 1)
      }
    }

    val r = cutpoints.windowed(2).filter { (a, b) -> a != b }.
      map { (a, b) -> IntRange(a, b - 1) }
    return r
  }

  fun makeDims(): List<List<IntRange>> {
    return (0..2).map { makeDim(it) }
  }

  fun idxs(dim: Int, inst: Instruction): List<Int> {
    val idim = inst.cuboid.dims[dim]
    return dims[dim].indices.filter { idim.contains(dims[dim][it].start) }
  }

  fun cuboidPtr(x: Int, y: Int, z: Int): Int {
    return x.shl(20) + y.shl(10) + z
  }

  fun ptrToIndices(ptr: Int): List<Int> {
    val mask = 1.shl(10) - 1
    return listOf(ptr.shr(20), ptr.shr(10), ptr).map { it.and(mask) }
  }

  fun doInstruction(inst: Instruction) {
    for (x in idxs(0, inst)) {
      for (y in idxs(1, inst)) {
        for (z in idxs(2, inst)) {
          val c = cuboidPtr(x, y, z)
          if (inst.on) {
            on.add(c)
          } else {
            on.remove(c)
          }
        }
      }
    }
  }

  fun cubesOn(): Long {
    return on.map { ptr ->
      val ranges = ptrToIndices(ptr).zip(0..2).map { (i, d) -> dims[d][i] }
      Cuboid(ranges).size()
    }.sum()
  }

  fun doAll(insts: List<Instruction>): Long {
    for (inst in insts) {
      println(inst)
      doInstruction(inst)
      println(on.size)
    }
    return cubesOn()
  }
}

fun main(args: Array<String>) {
  val insts = Instruction.parse(args.first())

  val part1 = Grid(insts, Cuboid(listOf(-50..50, -50..50, -50..50)))
  println(part1.doAll(insts))

  println(Grid(insts).doAll(insts))
}

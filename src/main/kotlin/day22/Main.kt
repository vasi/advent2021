package day22

import java.io.File

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

data class Cuboid(val dims: List<IntRange>) {
  fun size(): Long {
    return dims.map { (it.endInclusive - it.start + 1).toLong() }.reduce { a, b -> a * b }
  }

  fun mergedRanges(dim: Int, other: Cuboid): List<IntRange> {
    val cuts = listOf(dims[dim], other.dims[dim])
      .flatMap { listOf(it.start, it.endInclusive + 1) }.sorted()
    return cuts.windowed(2).filter { (a, b) -> a != b }
      .map { (a, b) -> a..(b-1) }
  }

  fun containsPoint(x: Int, y: Int, z: Int): Boolean {
    return dims[0].contains(x) && dims[1].contains(y) && dims[2].contains(z)
  }

  // yields the parts of this cuboid that are outside of other
  fun subtract(other: Cuboid): List<Cuboid> {
    val r = mutableListOf<Cuboid>()
    var allMatch = true
    for (x in mergedRanges(0, other)) {
      for (y in mergedRanges(1, other)) {
        for (z in mergedRanges(2, other)) {
          if (this.containsPoint(x.start, y.start, z.start)) {
            if (other.containsPoint(x.start, y.start, z.start)) {
              allMatch = false
            } else {
              r.add(Cuboid(listOf(x, y, z)))
            }
          }
        }
      }
    }
    if (allMatch) {
      return listOf(this) // everything inside this is outside other
    }
    return r
  }
}

class Grid {
  var on = listOf<Cuboid>()

  fun runInstruction(inst: Instruction) {
    val newCuboid = if (inst.on) listOf(inst.cuboid) else listOf()
    on = on.flatMap { it.subtract(inst.cuboid) } + newCuboid
  }

  fun cubesOn(): Long {
    return on.map { it.size() }.sum()
  }

  fun boundingInstructions(): List<Instruction> {
    val all = Int.MIN_VALUE..Int.MAX_VALUE
    val tooSmall = Int.MIN_VALUE .. -51
    val tooBig = 51..Int.MAX_VALUE
    return listOf(
      Cuboid(listOf(tooSmall, all, all)),
      Cuboid(listOf(tooBig, all, all)),
      Cuboid(listOf(all, tooSmall, all)),
      Cuboid(listOf(all, tooBig, all)),
      Cuboid(listOf(all, all, tooSmall)),
      Cuboid(listOf(all, all, tooBig)),
    ).map { Instruction(it, false) }
  }

  fun runAll(insts: List<Instruction>) {
    for (inst in insts) {
      runInstruction(inst)
    }
  }

  fun printAnswers(insts: List<Instruction>) {
    runAll(insts)
    val p2 = cubesOn()
    println(on.size)

    runAll(boundingInstructions())
    println(cubesOn())
    println(p2)
  }
}

fun main(args: Array<String>) {
  val insts = Instruction.parse(args.first())
  Grid().printAnswers(insts)
}

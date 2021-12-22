package day22

import java.io.File
import java.lang.Integer.max
import java.lang.Integer.min

data class Pos(val x: Int, val y: Int, val z: Int)

data class Cuboid(val p1: Pos, val p2: Pos) {
  fun intersectDim(p1min: Int, p1max: Int, p2min: Int, p2max: Int): IntRange {
    val min = max(p1min, p2min)
    val max = min(p1max, p2max)
    if (min > max) {
      return IntRange.EMPTY
    }
    return min..max
  }

  fun points(within: Cuboid): List<Pos> {
    val r = mutableListOf<Pos>()
    for (x in intersectDim(p1.x, p2.x, within.p1.x, within.p2.x)) {
      for (y in intersectDim(p1.y, p2.y, within.p1.y, within.p2.y)) {
        for (z in intersectDim(p1.z, p2.z, within.p1.z, within.p2.z)) {
          r.add(Pos(x, y, z))
        }
      }
    }
    return r
  }

  fun contains(pos: Pos): Boolean {
    return pos.x >= p1.x && pos.x <= p2.x &&
        pos.y >= p1.y && pos.y <= p2.y &&
        pos.z >= p1.z && pos.z <= p2.z
  }
}

data class Instruction(val cuboid: Cuboid, val on: Boolean) {
  companion object {
    fun parse(file: String): List<Instruction> {
      return File(file).readLines().map { line ->
        val (dir, coords) = line.split(" ")
        val on = dir == "on"
        val (x, y, z) = coords.split(",").map {
          val (p1, p2) = Regex(""".=(-?\d+)..(-?\d+)""").matchEntire(it)!!.destructured
          Pair(p1.toInt(), p2.toInt())
        }
        Instruction(Cuboid(
          Pos(x.first, y.first, z.first),
          Pos(x.second, y.second, z.second)
        ), on)
      }
    }
  }
}

data class Grid(val on: MutableSet<Pos> = mutableSetOf()) {
  fun run(inst: Instruction, within: Cuboid) {
    for (pos in inst.cuboid.points(within)) {
      if (inst.on) {
        on.add(pos)
      } else {
        on.remove(pos)
      }
    }
  }

  fun part1(insts: List<Instruction>): Int {
    val within = Cuboid(Pos(-50, -50, -50), Pos(50, 50,50))
    for (inst in insts) {
      run(inst, within)
    }
    return on.size
  }
}

fun main(args: Array<String>) {
  val insts = Instruction.parse(args.first())
  println(Grid().part1(insts))
}

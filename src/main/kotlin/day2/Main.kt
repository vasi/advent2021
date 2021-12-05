package day2

import java.io.File

abstract class Processor {
  abstract fun forward(i: Int)
  abstract fun up(i: Int)
  abstract fun down(i: Int)

  var depth = 0
  var x = 0
  fun result() = depth * x

  fun runLines(lines: List<String>): Processor {
    val re = Regex("""\A(forward|up|down) (\d+)\z""")
    lines.forEach { line ->
      val (cmd, istr) = re.matchEntire(line)!!.destructured
      val i = Integer.parseInt(istr)
      when (cmd) {
        "forward" -> forward(i)
        "up" -> up(i)
        "down" -> down(i)
      }
    }
    return this
  }
}

class Part1Processor : Processor() {
  override fun forward(i: Int) {
    x += i
  }

  override fun up(i: Int) {
    depth -= i
  }

  override fun down(i: Int) {
    depth += i
  }
}

class Part2Processor : Processor() {
  var aim = 0

  override fun forward(i: Int) {
    x += i
    depth += aim * i
  }

  override fun up(i: Int) {
    aim -= i
  }

  override fun down(i: Int) {
    aim += i
  }
}

fun main(args: Array<String>) {
  val lines = File(args.first()).readLines()
  println(Part1Processor().runLines(lines).result())
  println(Part2Processor().runLines(lines).result())
}

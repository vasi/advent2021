package day2

import java.io.File

fun main(args: Array<String>) {
  val commands = File(args.first()).readLines()

  val re = Regex("""\A(forward|up|down) (\d+)\z""")
  var depth = 0
  var x = 0
  commands.forEach { cmd ->
    val match = re.matchEntire(cmd)!!
    val (name, distStr) = match.destructured
    val dist = Integer.parseInt(distStr)
    when (name) {
      "forward" -> x += dist
      "up" -> depth -= dist
      "down" -> depth += dist
    }
  }
  println(depth * x)
}

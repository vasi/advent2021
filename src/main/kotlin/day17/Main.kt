package day17

data class Target(val xmin: Int, val xmax: Int, val ymin: Int, val ymax: Int) {
  fun contains(x: Int, y: Int): Boolean {
    return (x >= xmin) && (x <= xmax) && (y >= ymin) && (y <= ymax)
  }
}

fun intersects(xvel: Int, yvel: Int, target: Target): Boolean {
  var x = 0
  var y = 0
  var dx = xvel
  var dy = yvel

  while (true) {
    x += dx
    y += dy
    dx = if (dx == 0) 0 else dx - 1
    dy -= 1

    if (target.contains(x, y))
      return true

    if (x > target.xmax)
      return false
    if (y < target.ymin && dy < 0)
      return false
  }
}

fun allIntersecting(target: Target): List<Pair<Int, Int>> {
  val ret = mutableListOf<Pair<Int, Int>>()
  for (xvel in 0..target.xmax) {
    for (yvel in target.ymin .. -target.ymin) {
      if (intersects(xvel, yvel, target)) {
        ret.add(Pair(xvel, yvel))
      }
    }
  }
  return ret
}

fun part1(target: Target): Int {
  return allIntersecting(target).map { it.second }.toSet().maxOf { dy -> dy * (dy+1) / 2 }
}

fun part2(target: Target): Int {
  return allIntersecting(target).size
}

fun main(args: Array<String>) {
  val target = Target(156, 202, -110, -69)
//  val target = Target(20, 30, -10, -5)

  println(part1(target))
  println(part2(target))
}

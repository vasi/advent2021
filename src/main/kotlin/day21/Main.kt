package day21

interface Die {
  fun roll(): Int
}

class DieManager(val die: Die) {
  var timesRolled = 0

  fun total(): Int {
    val r = die.roll() + die.roll() + die.roll()
    timesRolled += 3
    return r
  }
}

class Deterministic : Die {
  var next = 1

  override fun roll(): Int {
    var r = next
    next = (next % 100) + 1
    return r
  }
}

data class Player(var pos: Int) {
  var score: Int = 0

  fun play(mgr: DieManager) {
    pos = (pos + mgr.total() - 1) % 10 + 1
    score += pos
  }
}

fun part1(pos1: Int, pos2: Int, die: Die): Int {
  val mgr = DieManager(die)
  val p1 = Player(pos1)
  val p2 = Player(pos2)

  while (true) {
    p1.play(mgr)
    if (p1.score >= 1000) break
    p2.play(mgr)
    if (p2.score >= 1000) break
  }

  return listOf(p1, p2).minOf { it.score } * mgr.timesRolled
}

fun main(args: Array<String>) {
  println(part1(4, 2, Deterministic()))
}

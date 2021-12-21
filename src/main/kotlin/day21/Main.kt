package day21

import kotlin.math.max

class CountingDie {
  var timesRolled = 0
  var next = 1

  fun roll(): Int {
    val r = next
    next = (next % 100) + 1
    timesRolled += 1
    return r
  }

  fun next(): Int {
    return roll() + roll() + roll()
  }
}


data class Player(val pos: Int, val score: Int = 0) {
  fun advance(roll: Int): Player {
    val p = (pos + roll - 1) % 10 + 1
    return Player(p, score + p)
  }
}

fun part1(pos1: Int, pos2: Int): Int {
  val die = CountingDie()
  var p1 = Player(pos1)
  var p2 = Player(pos2)

  while (true) {
    p1 = p1.advance(die.next())
    if (p1.score >= 1000) break
    p2 = p2.advance(die.next())
    if (p2.score >= 1000) break
  }

  return listOf(p1, p2).minOf { it.score } * die.timesRolled
}

data class Situation(val p1: Player, val p2: Player, val p1Next: Boolean) {
  fun nextPlayers(p: Player, roll: Map<Int, Long>): Map<Player, Long> {
    return roll.map { (r, c) -> p.advance(r) to c }.toMap()
  }

  fun next(roll: Map<Int, Long>): Map<Situation, Long> {
    if (p1Next)
      return nextPlayers(p1, roll).
        map { Situation(it.key, p2, false) to it.value }.toMap()
    else
      return nextPlayers(p2, roll).
      map { Situation(p1, it.key, true) to it.value }.toMap()
  }
}

// Return count of results per roll
fun diracResults(): Map<Int, Long> {
  val m = mutableMapOf<Int, Long>()
  for (a in 1..3) {
    for (b in 1..3) {
      for (c in 1..3) {
        m.compute(a + b + c) { _, cnt -> (cnt ?: 0) + 1 }
      }
    }
  }
  return m
}

data class Results(val w1: Long, val w2: Long) {
  fun add(r: Results): Results {
    return Results(w1 + r.w1, w2 + r.w2)
  }

  fun mul(f: Long): Results {
    return Results(w1 * f, w2 * f)
  }
}

class Part2 {
  val rolls = diracResults()
  val known = mutableMapOf<Situation, Results>()


  fun addScore(score1: Int, score2: Int, f: (Situation) -> Results?) {
    for (pos1 in 1..10) {
      for (pos2 in 1..10) {
        val p1 = Player(pos1, score1)
        val p2 = Player(pos2, score2)
        for (n in listOf(true, false)) {
          val s = Situation(p1, p2, n)
          val r = f(s)
          if (r != null)
            known[s] = r
        }
      }
    }
  }

  init {
    // Fill in some known positions
    for (s1 in 0..31) {
      for (s2 in 0 .. 31) {
        addScore(s1, s2) {
          if (s2 >= 21 && it.p1Next)
            Results(0, 1)
          else if (s1 >= 21 && !it.p1Next)
            Results(1, 0)
          else null
        }
      }
    }
  }

  fun addDerivedScore(score1: Int, score2: Int) {
    addScore(score1, score2) { s ->
      var r = Results(0, 0)
      val sss = s.next(rolls)
      for ((ss, cnt) in sss) {
        val sr = known[ss]
        r = r.add(sr!!.mul(cnt))
      }
      r
    }
  }

  fun run(pos1: Int, pos2: Int): Long {
    for (total in 40 downTo 0) {
      for (s1 in 0..20) {
        val s2 = total - s1
        if (s2 >= 0 && s2 <= 20) {
          addDerivedScore(s1, s2)
        }
      }
    }

    val r = known[Situation(Player(pos1), Player(pos2), true)]!!
    return max(r.w1, r.w2)
  }
}

fun main() {
  println(part1(4, 2))
  println(Part2().run(4, 2))
}

package day18

import java.io.File
import java.lang.Long.max

class CharStream(val s: String) {
  var i = 0

  fun peek(): Char? {
    if (i >= s.length)
      return null
    return s[i]
  }

  fun take(): Char {
    val r = s[i]
    i += 1
    return r
  }

  fun assert(c: Char) {
    val t = take()
    if (t != c) {
      throw RuntimeException("unexpected character $t")
    }
  }

  fun takeWhile(f: (Char) -> Boolean): List<Char> {
    val r = mutableListOf<Char>()
    while (peek() != null && f(peek()!!))
      r.add(take())
    return r
  }
}

enum class Direction {
  LEFT {
    override fun child(n: Number): Number? {
      return n.a
    }

    override fun opposite(): Direction {
      return RIGHT
    }
  },
  RIGHT {
    override fun child(n: Number): Number? {
      return n.b
    }

    override fun opposite(): Direction {
      return LEFT
    }
  };

  abstract fun child(n: Number): Number?
  abstract fun opposite(): Direction
}

class Number(var v: Long = 0, var a: Number? = null, var b: Number? = null, var parent: Number? = null) {
  init {
    a?.parent = this
    b?.parent = this
  }

  companion object {
    fun parseStream(s: CharStream): Number {
      val digits = s.takeWhile { it.isDigit() }
      if (digits.isNotEmpty()) {
        return Number(v = digits.joinToString().toLong())
      }

      s.assert('[')
      val a = parseStream(s)
      s.assert(',')
      val b = parseStream(s)
      s.assert(']')
      return Number(a = a, b = b)
    }

    fun parse(s: String): Number {
      return parseStream(CharStream(s))
    }

    fun sum(nums: List<String>): Number {
      return nums.map { parse(it) }.reduce { a, n ->
        a.add(n)
      }
    }
  }

  fun isPair(): Boolean {
    return a != null
  }

  override fun toString() : String {
    if (isPair()) {
      return "[$a,$b]"
    } else {
      return v.toString()
    }
  }

  fun splitOne(): Boolean {
    if (isPair()) {
      if (a!!.splitOne())
        return true
      return b!!.splitOne()
    } else if (v < 10) {
      return false
    } else {
      a = Number(v = v / 2, parent = this)
      b = Number(v = (v + 1) / 2, parent = this)
      return true
    }
  }

  fun addDirection(n: Long, dir: Direction) {
    var prev = this
    var cur = this.parent

    // go up until we can go in our direction
    while (cur != null && dir.child(cur) == prev) {
      prev = cur
      cur = cur.parent
    }

    // go in our direction
    if (cur == null)
      return
    cur = dir.child(cur)

    // go otherDirection as far as possible
    while (cur!!.isPair()) {
      cur = dir.opposite().child(cur)
    }

    // do the add
    cur.v += n
  }

  fun explodeOne(depth: Int = 0): Boolean {
    if (!isPair()) {
      return false
    }
    if (depth < 4 || a!!.isPair() || b!!.isPair()) {
      if (a?.explodeOne(depth + 1) ?: false)
        return true
      return b?.explodeOne(depth + 1) ?: false
    }

    addDirection(a!!.v, Direction.LEFT)
    addDirection(b!!.v, Direction.RIGHT)
    a = null
    b = null
    v = 0
    return true
  }

  fun reduceOne(): Boolean {
    return explodeOne() || splitOne()
  }

  fun reduce() {
    while (reduceOne()) {
      // pass
    }
  }

  fun add(n: Number): Number {
    val r = Number(a = this, b = n)
    r.reduce()
    return r
  }

  fun magnitude(): Long {
    if (isPair()) {
      return 3 * a!!.magnitude() + 2 * b!!.magnitude()
    } else {
      return v
    }
  }
}

fun part2(nums: List<String>): Long {
  var best = 0L
  for (a in nums) {
    for (b in nums) {
      val sum = Number.sum(listOf(a, b))
      best = max(best, sum.magnitude())
    }
  }
  return best
}

fun main(args: Array<String>) {
  val nums = File(args.first()).readLines()
  println(Number.sum(nums).magnitude())
  println(part2(nums))
}

package day12

import java.io.File

data class Cave(val edges: List<Pair<String, String>>) {
  companion object {
    fun parse(file: String): Cave {
      val pairs = File(file).readLines().map { l ->
        val (a, b) = l.split("-")
        Pair(a, b)
      }
      return Cave(pairs)
    }
  }

  val links: Map<String, List<String>> = run {
    val h = mutableMapOf<String, MutableList<String>>()
    edges.forEach { (a, b) ->
      h.putIfAbsent(a, mutableListOf())
      h.putIfAbsent(b, mutableListOf())
      h[a]!!.add(b)
      h[b]!!.add(a)
    }
    h
  }

  fun isLower(s: String): Boolean {
    return s.lowercase() == s
  }

  fun isStartEnd(s: String): Boolean {
    return s == "start" || s == "end"
  }

  fun countPaths(sofar: MutableList<String>, twiceOk: Boolean, twiceSeen: Boolean): Long {
    val cur = sofar.last()
    if (cur == "end") {
      return 1
    }

    var found = 0L
    for (adj in links[cur]!!) {
      var twSeen = twiceSeen
      if (isLower(adj) && sofar.contains(adj)) {
        if (isStartEnd(adj) || !twiceOk || twiceSeen)
          continue
        twSeen = true
      }

      sofar.add(adj)
      found += countPaths(sofar, twiceOk, twSeen)
      sofar.removeLast()
    }
    return found
  }

  fun part1(): Long {
    return countPaths(mutableListOf("start"), false, false)
  }

  fun part2(): Long {
    return countPaths(mutableListOf("start"), true, false)
  }
}

fun main(args: Array<String>) {
  val cave = Cave.parse(args.first())
  println(cave.part1())
  println(cave.part2())
}


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

  fun countPaths(sofar: MutableList<String>): Long {
    val cur = sofar.last()
    if (cur == "end") {
      return 1
    }

    var found = 0L
    for (adj in links[cur]!!) {
      if (!isLower(adj) || !sofar.contains(adj)) {
        sofar.add(adj)
        found += countPaths(sofar)
        sofar.removeLast()
      }
    }
    return found
  }

  fun part1(): Long {
    return countPaths(mutableListOf("start"))
  }
}

fun main(args: Array<String>) {
  val cave = Cave.parse(args.first())
  println(cave.part1())
}


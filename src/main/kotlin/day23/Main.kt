package day23

import java.util.*

data class Space(val edges: MutableList<Int>, val target: Char? = null)

data class Layout(val spaces: List<Space>) {
  val costs = mapOf(
    'A' to 1,
    'B' to 10,
    'C' to 100,
    'D' to 1000,
  )

  companion object {
    fun layout(part2: Boolean): Layout {
      val spaces = mutableListOf(
        // rooms, top to bottom
        Space(mutableListOf(1, 10), 'A'), // 0
        Space(mutableListOf(0), 'A'),
        Space(mutableListOf(3, 12), 'B'), // 2
        Space(mutableListOf(2), 'B'),
        Space(mutableListOf(5, 14), 'C'), // 4
        Space(mutableListOf(4), 'C'),
        Space(mutableListOf(7, 16), 'D'), // 6
        Space(mutableListOf(6), 'D'),
        // hallway, left to right
        Space(mutableListOf(9)), // 8
        Space(mutableListOf(8, 10)),
        Space(mutableListOf(9, 11, 0)), // 10 - outside A
        Space(mutableListOf(10, 12)),
        Space(mutableListOf(11, 13, 2)), // 12 - outside B
        Space(mutableListOf(12, 14)),
        Space(mutableListOf(13, 15, 4)), // 14 - outside C
        Space(mutableListOf(14, 16)),
        Space(mutableListOf(15, 17, 6)), // 16 - outside D
        Space(mutableListOf(16, 18)),
        Space(mutableListOf(17)), // 18
      )
      if (part2) {
        for (i in 0..6 step 2) {
          val s = spaces.size
          spaces.add(Space(mutableListOf(i, s + 1), spaces[i].target))
          spaces.add(Space(mutableListOf(i + 1, s), spaces[i].target))
          spaces[i].edges.remove(i+1)
          spaces[i].edges.add(s)
          spaces[i+1].edges.remove(i)
          spaces[i+1].edges.add(s+1)
        }
      }
      return Layout(spaces)
    }
  }

  fun wantPosition(): String {
    return spaces.map { it.target ?: "." }.joinToString("")
  }

  fun isOutsideRoom(pos: Int): Boolean {
    return spaces[pos].edges.size == 3
  }

  // where could we possibly go? map of new space to number of steps
  fun availableMoves(position: String, pos: Int): Map<Int, Int> {
    val ret = mutableMapOf<Int, Int>()
    val todo = mutableListOf(pos)
    while (todo.isNotEmpty()) {
      val p = todo.removeFirst()
      val steps = if (p == pos) 0 else ret[p]!!
      for (r in spaces[p].edges) {
        if (r != pos && position[r] == '.' && !ret.contains(r)) {
          ret[r] = steps + 1
          todo.add(r)
        }
      }
    }
    return ret
  }

  fun legalMove(position: String, start: Int, end: Int): Boolean {
    val sr = spaces[start]
    val er = spaces[end]
    val cur = position[start]

    if (isOutsideRoom(end)) {
      return false // outside room
    } else if (er.target != null && er.target != cur) {
      return false // wrong room
    } else if (sr.target == null && er.target == null) {
      return false // hallway to hallway
    } else {
      return true
    }
  }

  fun moveTo(position: String, start: Int, end: Int): String {
    val a = position.toCharArray()
    a[end] = a[start]
    a[start] = '.'
    return a.joinToString("")
  }

  // Yield moves and costs
  fun legalMoves(position: String, pos: Int): Map<String, Int> {
    val cur = position[pos]
    if (cur == '.') {
      return mapOf()
    }

    return availableMoves(position, pos)
      .filter { (e, _) -> legalMove(position, pos, e) }
      .map { (e, s) -> moveTo(position, pos, e) to s * costs[cur]!! }
      .toMap()
  }

  data class WeightedPos(val pos: String, val cost: Int) : Comparable<WeightedPos> {
    override fun compareTo(other: WeightedPos): Int {
      return cost.compareTo(other.cost)
    }
  }

  fun bestCost(startPosition: String): Int {
    val complete = mutableSetOf<String>()
    val wantPosition = wantPosition()
    val todo = PriorityQueue<WeightedPos>()
    todo.add(WeightedPos(startPosition, 0))
    while (todo.isNotEmpty()) {
      val next = todo.remove()
      if (complete.contains(next.pos)) {
        continue
      }
      if (next.pos == wantPosition) {
        return next.cost
      }

      complete.add(next.pos)
      for (i in 0 until spaces.size) {
        for ((move, cost) in legalMoves(next.pos, i)) {
          if (!complete.contains(move)) {
            todo.add(WeightedPos(move, cost + next.cost))
          }
        }
      }
    }
    throw RuntimeException("???")
  }
}

fun main() {
  val sampleInput = "BACDBCDA..........."
  val actualInput = "DBCCADBA..........."
  val append = "DDCBBAAC"

  println(Layout.layout(false).bestCost(sampleInput))
  println(Layout.layout(true).bestCost(sampleInput + append))
}

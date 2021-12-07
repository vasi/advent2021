package day6

import java.io.File

typealias Fishes = Map<Int, Long>

fun nextGen(fish: Fishes): Fishes {
  return fish
    .flatMap { (k, v) ->
      if (k == 0)
        listOf(6 to v, 8 to v)
      else listOf(k - 1 to v)
    }
    .groupingBy { it.first }
    .fold(0) { a, e -> a + e.second }
}

fun simulate(fish: Fishes, generations: Int): Fishes {
  var fs = fish
  repeat(generations) {
    fs = nextGen(fs)
  }
  return fs
}

fun countFish(fish: Fishes): Long {
  return fish.values.sum()
}

fun parseFish(file: String): Fishes {
  val ages = File(file).readLines().first().split(",")
    .map { Integer.parseInt(it) }
  return ages.groupingBy { it }.eachCount().mapValues { (_, v) -> v.toLong() }
}

fun main(args: Array<String>) {
  val fish = parseFish(args.first())
  println(countFish(simulate(fish, 80)))
  println(countFish(simulate(fish, 256)))
}

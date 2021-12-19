package day19

import java.io.File

data class Pos(val coords: List<Int>)

fun combinationsHelper(sofar: MutableList<Int>, choose: Int, avail: List<Int>, idx: Int, f: (List<Int>) -> Boolean): Boolean {
  if (choose == 0) {
    return f(sofar)
  } else if (choose > avail.size - idx) {
    return false
  } else {
    sofar.add(avail[idx])
    if (combinationsHelper(sofar, choose - 1, avail, idx + 1, f))
      return true
    sofar.removeLast()
    return combinationsHelper(sofar, choose, avail, idx + 1, f)
  }
}

fun combinations(list: List<Int>, choose: Int, f: (List<Int>) -> Boolean): Boolean {
  return combinationsHelper(mutableListOf(), choose, list, 0, f)
}

data class ScannerReport(val name: String, val beacons: List<Pos>) {
  fun forCanonicalSequences(dim: Int, dir: Int, f: (String) -> Boolean): Boolean {
    val vs = beacons.map { it.coords[dim] }.map { it * dir }.sorted()
    return combinations(vs, 12) { combo ->
      f(combo.map { it - combo.first() }.joinToString(","))
    }
  }

  fun canonicalSequences(dim: Int, dir: Int): Set<String> {
    val ret = mutableSetOf<String>()
    forCanonicalSequences(dim, dir) { ret.add(it); false }
    return ret
  }
}

data class Report(val scanners: List<ScannerReport>) {
  companion object {
    fun parse(file: String): Report {
      val scanners = mutableListOf<ScannerReport>()
      var name: String? = null
      var beacons = mutableListOf<Pos>()
      val nameRe = Regex("""--- scanner (\w+) ---""")

      for (line in File(file).readLines()) {
        if (line.isEmpty()) {
          continue
        }

        val nameMatch = nameRe.matchEntire(line)
        if (nameMatch != null) {
          if (name != null && beacons.isNotEmpty()) {
            scanners.add(ScannerReport(name, beacons))
            beacons = mutableListOf()
          }

          name = nameMatch.groupValues[1]
          continue
        }

        val coords = line.split(',').map { it.toInt() }
        beacons.add(Pos(coords))
      }
      return Report(scanners)
    }
  }
}

fun main(args: Array<String>) {
  val report = Report.parse(args.first())

  val canon = report.scanners.first().canonicalSequences(0, 1)

  val second = report.scanners[1]
  for (dim in 0..2) {
    for (dir in listOf(-1, 1)) {
      println("Trying dim=$dim dir=$dir")
      if (second.forCanonicalSequences(dim, dir) { canon.contains(it) }) {
        println("Found it!")
      }
    }
  }
}

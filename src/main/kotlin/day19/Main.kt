package day19

import java.io.File
import kotlin.math.absoluteValue

data class Pos(val coords: List<Int>) {
  fun toVector(other: Pos): Vector {
    return Vector(coords.zip(other.coords).map { (a, b) -> (a - b) })
  }
}

data class Vector(val coords: List<Int>) {
  val canon: List<Int> by lazy {
    coords.map { it.absoluteValue }.sorted()
  }

  override fun hashCode(): Int {
    return canon.hashCode()
  }

  override fun equals(other: Any?): Boolean {
    return (other is Vector) && canon.equals(other.canon)
  }
}

fun tri(n: Int): Int {
  return n * (n + 1) / 2
}

data class ScannerReport(val name: String, val beacons: List<Pos>) {
  companion object {
    val OVERLAPPING_INTERSECTIONS = tri(12 - 1)
  }

  fun vectors(): Set<Vector> {
    val vs = mutableSetOf<Vector>()
    for (i in 0 until beacons.size - 1) {
      for (j in i+1 until beacons.size) {
        vs.add(beacons[i].toVector(beacons[j]))
      }
    }

    if (vs.size != tri(beacons.size - 1)) {
      throw RuntimeException("oh shit, duplicate vector")
    }
    return vs
  }

  fun intersects(other: ScannerReport): Boolean {
    val intersections = vectors().intersect(other.vectors()).size
    return intersections >= OVERLAPPING_INTERSECTIONS
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
      if (name != null && beacons.isNotEmpty()) {
        scanners.add(ScannerReport(name, beacons))
      }

      return Report(scanners)
    }
  }
}

fun main(args: Array<String>) {
  val report = Report.parse(args.first())

  val scanners = report.scanners
  for (i in 0 until scanners.size-1) {
    for (j in i+1 until scanners.size) {
      if (scanners[i].intersects(scanners[j])) {
        println("Intersection: ${scanners[i].name} - ${scanners[j].name}")
      }
    }
  }
}

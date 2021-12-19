package day19

import java.io.File
import kotlin.math.absoluteValue

data class Pos(val coords: List<Int>) : Comparable<Pos> {
  override fun compareTo(other: Pos): Int {
    for (i in 0 until coords.size) {
      val c = coords[i].compareTo(other.coords[i])
      if (c != 0)
        return c
    }
    return 0
  }

  override fun toString(): String {
    return coords.joinToString(",")
  }

  fun toVector(other: Pos): Vector {
    return Vector(coords.zip(other.coords).map { (a, b) -> (a - b) }, listOf(this, other))
  }

  fun rotate(v1: Vector, v2: Vector): Pos {
    val newCoords = v1.coords.map { v ->
      val i = v2.coords.indexOfFirst { it.absoluteValue == v.absoluteValue }
      val mult = v2.coords[i] / v
      coords[i] * mult
    }
    return Pos(newCoords)
  }

  fun orient(v1: Vector, v2: Vector, translate: Vector): Pos {
    val cs = rotate(v1, v2).coords.zip(translate.coords).map { (a, b) -> a + b }
    return Pos(cs)
  }
}

data class Vector(val coords: List<Int>, val sources: List<Pos>) {
  val canon: List<Int> by lazy {
    coords.map { it.absoluteValue }.sorted()
  }

  override fun hashCode(): Int {
    return canon.hashCode()
  }

  override fun equals(other: Any?): Boolean {
    return (other is Vector) && canon.equals(other.canon)
  }

  fun helpfulForOrientation(): Boolean {
    return canon.toSet().size == 3
  }

  fun translation(other: Vector): Vector {
    val p1 = sources.minByOrNull { it.coords.first() }!!
    val rotated = other.sources.map { it.rotate(this, other) }
    val p2 = rotated.minByOrNull { it.coords.first() }!!
    return Vector(p1.coords.zip(p2.coords).map { (a, b) -> a - b }, listOf())
  }

  override fun toString(): String {
    return coords.joinToString(",")
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

  fun tryOrient(@Suppress("UNUSED_PARAMETER") towards: ScannerReport,
                towardsVectors: Set<Vector>): ScannerReport? {
    val myVectors = vectors()
    val intersecting = towardsVectors.intersect(myVectors)
    if (intersecting.size < OVERLAPPING_INTERSECTIONS) {
      return null // no overlap
    }

    // Find all implicated beacons

    // Find two vectors that share a node

    // Find the other's equivalent of one of our vectors
    val v = intersecting.find { it.helpfulForOrientation() }!!
    val o = myVectors.find { it == v }!!
    val translation = v.translation(o)

    return ScannerReport(name, beacons.map { it.orient(v, o, translation) })
  }

  override fun toString(): String {
    return "--- scanner $name ----\n" + beacons.sorted().joinToString("\n")
  }

  fun assertCommon(other: ScannerReport) {
    val common = this.beacons.intersect(other.beacons).size
    if (common < 12) {
      throw RuntimeException("Only $common beacons overlap between $name and ${other.name}")
    }
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

  fun reorient(): Report {
    val done = mutableListOf<ScannerReport>()
    val oriented = mutableListOf(scanners.first())
    val unoriented = scanners.drop(1).toMutableList()

    while (unoriented.isNotEmpty()) {
      val basis = oriented.removeLast()
      val vectors = basis.vectors()
      done.add(basis)

      unoriented.removeAll { u ->
        val fixed = u.tryOrient(basis, vectors)
        if (fixed != null) {
          basis.assertCommon(fixed)
          oriented.add(fixed)
          true
        } else {
          false
        }
      }
    }

    return Report(done + oriented)
  }

  fun countBeacons(): Int {
    return reorient().scanners.flatMap { it.beacons }.toSet().size
  }
}

fun main(args: Array<String>) {
  val report = Report.parse(args.first())
  println(report.countBeacons())
}

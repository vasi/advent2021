package day19

import java.io.File
import java.lang.Integer.max
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

  fun transform(transforms: List<Transform>): Pos {
    return Pos(transforms.map { t ->
      coords[t.dim] * t.dir + t.trans
    })
  }

  fun manhattanDistance(other: Pos): Int {
    return coords.zip(other.coords).map { (a, b) -> (a - b).absoluteValue }.sum()
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

  override fun toString(): String {
    return coords.joinToString(",")
  }
}

fun tri(n: Int): Int {
  return n * (n + 1) / 2
}

data class Transform(val dim: Int, val dir: Int, val trans: Int)

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

  fun beaconsInIntersection(vectors: Collection<Vector>): Set<Pos> {
    val sources = vectors.flatMap { it.sources }
    val counted = sources.groupBy { it }.mapValues { (_, v) -> v.size }
    val filtered = counted.filter { (_, v) -> v == 11 }.keys
    return filtered
  }

  fun dimTransform(vs: Collection<Int>, beacons: Collection<Pos>): Transform {
    val sorted = vs.sorted()
    val sig = sorted.map { it - sorted.first() }

    for (dim in 0..2) {
      for (dir in listOf(1, -1)) {
        val bsorted = beacons.map { it.coords[dim] * dir }.sorted()
        val bsig = bsorted.map { it - bsorted.first() }
        if (sig == bsig) {
          return Transform(dim, dir, sorted.first() - bsorted.first())
        }
      }
    }
    throw RuntimeException("No matching transform!")
  }

  fun calculateTransform(baseBeacons: Collection<Pos>, myBeacons: Collection<Pos>): List<Transform> {
    if (baseBeacons.size != 12 || myBeacons.size != 12) {
      throw RuntimeException("bad number of beacons")
    }
    return (0..2).map { d ->
      dimTransform(baseBeacons.map { it.coords[d] }, myBeacons)
    }
  }

  data class OrientResult(val scanner: ScannerReport, val scannerPos: Pos)

  fun tryOrient(@Suppress("UNUSED_PARAMETER") towards: ScannerReport,
                towardsVectors: Set<Vector>): OrientResult? {
    val myVectors = vectors()
    val intersecting = towardsVectors.intersect(myVectors)
    if (intersecting.size < OVERLAPPING_INTERSECTIONS) {
      return null // no overlap
    }

    // Find all implicated beacons
    val transforms = calculateTransform(
      beaconsInIntersection(intersecting),
      beaconsInIntersection(myVectors.filter { intersecting.contains(it) })
    )

    return OrientResult(
      ScannerReport(name, beacons.map { it.transform(transforms) }),
      Pos(transforms.map { it.trans })
    )
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

  data class ReportOrientResult(val report: Report, val scannerPositions: List<Pos>)

  fun reorient(): ReportOrientResult {
    val done = mutableListOf<ScannerReport>()
    val oriented = mutableListOf(scanners.first())
    val unoriented = scanners.drop(1).toMutableList()
    val positions = mutableListOf(Pos(listOf(0, 0, 0)))

    while (unoriented.isNotEmpty()) {
      val basis = oriented.removeLast()
      val vectors = basis.vectors()
      done.add(basis)

      unoriented.removeAll { u ->
        val fixed = u.tryOrient(basis, vectors)
        if (fixed != null) {
          basis.assertCommon(fixed.scanner)
          oriented.add(fixed.scanner)
          positions.add(fixed.scannerPos)
          true
        } else {
          false
        }
      }
    }

    return ReportOrientResult(Report(done + oriented), positions)
  }

  fun countBeacons(): Int {
    return scanners.flatMap { it.beacons }.toSet().size
  }
}

fun maxDistance(positions: List<Pos>): Int {
  var best = 0

  for (p1 in positions) {
    for (p2 in positions) {
      val dist = p1.manhattanDistance(p2)
      best = max(best, dist)
    }
  }
  return best
}

fun main(args: Array<String>) {
  val report = Report.parse(args.first())
  val result = report.reorient()
  println(result.report.countBeacons())
  println(maxDistance(result.scannerPositions))
}

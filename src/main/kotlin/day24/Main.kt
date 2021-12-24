package day24

import java.io.File

class Checksum {
  companion object {
    val L = listOf(1, 1, 1, 1, 26, 1, 1, 26, 1, 26, 26, 26, 26, 26)
    val M = listOf(13, 15, 15, 11, -7, 10, 10, -5, 15, -3, 0, -5, -9, 0)
    val N = listOf(6, 7, 10, 2, 15, 8, 1, 10, 5, 3, 5, 11, 12, 10)

    fun checksum(model: Long): Boolean {
      val digits = String.format("%014d", model).toCharArray().map { it - '0' }
      var z = 0
      for (i in digits.indices) {
        if (z % 26 + M[i] == digits[i]) {
          z /= L[i]
        } else {
          z = (z / L[i]) * 26 + (digits[i] + N[i])
        }
      }
      return z == 0
    }
  }
}

fun main(args: Array<String>) {
  println(Checksum.checksum(39494195799979))
}

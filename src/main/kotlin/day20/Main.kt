package day20

import java.io.File

data class Pos(val x: Int, val y: Int) {
  fun adjacent(): List<Pos> {
    val r = mutableListOf<Pos>()
    for (dy in -1..1) {
      for (dx in -1..1) {
        r.add(Pos(x+dx, y+dy))
      }
    }
    return r
  }
}

data class Enhancement(val mapping: List<Int>) {
  companion object {
    fun parse(line: String): Enhancement {
      val mapping = line.toCharArray().map { if (it == '#') 1 else 0 }
      return Enhancement(mapping)
    }
  }
}

data class Image(val bg: Int, val pix: List<List<Int>>) {
  companion object {
    fun parse(lines: List<String>): Image {
      val pix = lines.map { l -> l.map { if (it == '#') 1 else 0  } }
      return Image(0, pix)
    }
  }

  fun height(): Int {
    return pix.size
  }
  fun width(): Int {
    return pix.first().size
  }

  fun at(pos: Pos): Int {
    if (pos.x < 0 || pos.x >= width() || pos.y < 0 || pos.y >= height())
      return bg
    return pix[pos.y][pos.x]
  }

  fun nextValue(algo: Enhancement, pos: Pos): Int {
    val i = pos.adjacent().map { at(it) }.joinToString("").toInt(2)
    return algo.mapping[i]
  }

  fun evolve(algo: Enhancement): Image {
    val nextBg = if (algo.mapping[0] == 1) (1-bg) else bg
    val nextPix = (-1..height()).map { y ->
      (-1..width()).map { x ->
        nextValue(algo, Pos(x, y))
      }
    }
    return Image(nextBg, nextPix)
  }

  fun litPixels(): Int {
    return pix.sumOf { line -> line.count { it == 1 } }
  }

  override fun toString(): String {
    return pix.map { row ->
      row.map { if (it == 1) '#' else '.' }.joinToString("")
    }.joinToString("\n")
  }
}

data class Input(val img: Image, val algo: Enhancement) {
  companion object {
    fun parse(file: String): Input {
      val lines = File(file).readLines().toMutableList()
      val algo = Enhancement.parse(lines.removeFirst())
      lines.removeFirst()
      val img = Image.parse(lines)
      return Input(img, algo)
    }
  }

  fun enhance(n: Int): Image {
    var i = img
    repeat(n) {
      i = i.evolve(algo)
    }
    return i
  }
}

fun main(args: Array<String>) {
  val input = Input.parse(args.first())
  println(input.enhance(2).litPixels())
  println(input.enhance(50).litPixels())
}

package day16

import java.io.File
import java.lang.RuntimeException

abstract class Value {
  open fun packets(): List<Packet> {
    return emptyList()
  }

  abstract fun value(): Long
}

data class Literal(val literal: Long) : Value() {
  companion object {
    fun parse(bs: BitStream): Literal {
      var ret = 0L
      while (true) {
        val hasMore = bs.readBit() == 1
        val digit = bs.readInt(4)
        ret = ret.shl(4) + digit
        if (!hasMore) break
      }
      return Literal(ret)
    }
  }

  override fun value(): Long {
    return literal
  }
}

data class Operator(val op: Int, val packets: List<Packet>) : Value() {
  companion object {
    fun parse(op: Int, bs: BitStream): Operator {
      val packets = mutableListOf<Packet>()
      val lengthType = bs.readBit()
      if (lengthType == 0) {
        val subPacketSize = bs.readInt(15)
        val start = bs.offset()
        while (bs.offset() < start + subPacketSize) {
          packets.add(Packet.parse(bs))
        }
      } else {
        val subPackets = bs.readInt(11)
        repeat(subPackets) {
          packets.add(Packet.parse(bs))
        }
      }
      return Operator(op, packets)
    }
  }

  override fun packets(): List<Packet> {
    return packets
  }

  private fun nth(i: Int): Long {
    return packets[i].value.value()
  }

  private fun values(): List<Long> {
    return packets.map { it.value.value() }
  }

  private fun cmp(p : (Long, Long) -> Boolean): Long {
    if (p(nth(0), nth(1))) return 1
    else return 0
  }

  override fun value(): Long {
    return when (op) {
      0 -> values().sum()
      1 -> values().reduce { a, b -> a * b }
      2 -> values().minOrNull()!!
      3 -> values().maxOrNull()!!
      5 -> cmp { a, b -> a > b }
      6 -> cmp { a, b -> a < b }
      7 -> cmp { a, b -> a == b }
      else -> throw RuntimeException("op $op out of range")
    }
  }
}

data class Packet(val version: Int, val type: Int, val value: Value) {
  companion object {
    fun parse(bs: BitStream): Packet {
      val version = bs.readInt(3)
      val type = bs.readInt(3)
      val value = if (type == 4) Literal.parse(bs)
        else Operator.parse(type, bs)
      return Packet(version, type, value)
    }
  }
}


class BitStream(hex: String) {
  private val bytes = hex.map { it.digitToInt(16) }
  private var idx = 0
  private var bit = 3

  fun offset(): Int {
    return (idx * 4) + (3 - bit)
  }

  fun readBit(): Int {
    val ret = bytes[idx].shr(bit).and(1)
    if (bit == 0)
      idx += 1
    bit = (bit + 3) % 4
    return ret
  }

  fun readInt(bits: Int): Int {
    var r = 0
    repeat(bits) {
      r = r.shl(1) + readBit()
    }
    return r
  }
}

fun versionSum(packet: Packet): Long {
  return packet.version + packet.value.packets().sumOf { versionSum(it) }
}

fun main(args: Array<String>) {
  val type = args[0]
  val contents = if (type == "file")
      File(args[1]).readLines().first()
    else args[1]

  val bs = BitStream(contents)
  val packet = Packet.parse(bs)
  println(versionSum(packet))
  println(packet.value.value())
}

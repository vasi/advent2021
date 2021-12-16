package day16

import java.io.File

open class Value {
  open fun packets(): List<Packet> {
    return emptyList()
  }
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
}

data class Operator(val packets: List<Packet>) : Value() {
  companion object {
    fun parse(bs: BitStream): Operator {
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
      return Operator(packets)
    }
  }

  override fun packets(): List<Packet> {
    return packets
  }
}

data class Packet(val version: Int, val type: Int, val value: Value) {
  companion object {
    fun parse(bs: BitStream): Packet {
      val version = bs.readInt(3)
      val type = bs.readInt(3)
      val value = if (type == 4) Literal.parse(bs)
        else Operator.parse(bs)
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
  return packet.version + packet.value.packets().map { versionSum(it) }.sum()
}

fun main(args: Array<String>) {
  val type = args[0]
  val contents = if (type == "file")
      File(args[1]).readLines().first()
    else args[1]

  val bs = BitStream(contents)
  val packet = Packet.parse(bs)
  println(versionSum(packet))
}

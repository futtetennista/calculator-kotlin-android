package com.stefanodacchille.playground

import android.os.Parcel
import android.os.Parcelable

data class DisplayNumber(val negative: Boolean = false, val value: String, val percent: Int = 0) :
    Parcelable {

  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeInt(if (negative) 0 else 1)
    dest.writeString(value)
    dest.writeInt(percent)
  }

  override fun describeContents(): Int {
    return 0
  }

  fun toDisplay(): String {
    var decimalValue = value.toDouble()

    if (decimalValue.isNaN()) {
      return "Not a number"
    }
    if (decimalValue.isInfinite()) {
      return "You asked me too much"
    }

    if (this.negative) {
      decimalValue = decimalValue.minus()
    }
    for (i in 1..this.percent) {
      decimalValue /= 100
    }

    return if (decimalValue.isInteger()) {
      decimalValue.toString().takeWhile { it != '.' }
    } else {
      decimalValue.toString()
    }
  }

  fun toDecimal(): Double {
    var baseValue: Double = value.toDouble()
    if (this.negative) {
      baseValue *= -1
    }
    if (this.percent > 0) {
      baseValue /= this.percent
    }

    return baseValue
  }

  companion object {

    var CREATOR = object : Parcelable.Creator<DisplayNumber> {
      override fun newArray(size: Int): Array<out DisplayNumber>? {
        return newArray(size)
      }

      override fun createFromParcel(source: Parcel): DisplayNumber? {
        return DisplayNumber(source.readInt() > 0, source.readString(), source.readInt())
      }
    }

    val zero = DisplayNumber(value = "0")

    fun fromDecimal(number: Double): DisplayNumber {
      val negative = number < 0
      val numberAsString = number.toString()
      val value = if (negative) numberAsString.drop(1) else numberAsString
      return DisplayNumber(negative, value, 0)
    }
  }
}

fun Double.isInteger(): Boolean {
  return Math.floor(this) == this && !isNaN() && !isInfinite()
}

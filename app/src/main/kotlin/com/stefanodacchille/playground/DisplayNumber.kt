package com.stefanodacchille.playground

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

data class DisplayNumber(val negative: Boolean, val value: String, val percent: Int) : Parcelable {

  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeInt(if (negative) 0 else 1)
    dest.writeString(value)
    dest.writeInt(percent)
  }

  override fun describeContents(): Int {
    return 0
  }

  fun toDisplay(): String {
    var displayValue = value.toFloat()
    if (this.negative) {
      displayValue = value.toFloat().minus()
    }
    for (i in 1..this.percent) {
      displayValue /= 100
    }
    return displayValue.toString()
  }

  fun toFloat(): Float {
    var baseValue: Float = value.toFloat()
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

    val zero = DisplayNumber(negative = false, value = "0", percent = 0)

    fun fromBundle(s: Bundle?): DisplayNumber {
      if (s == null) return zero

      return DisplayNumber(negative = s.getBoolean("extra_displaynumber_negative"),
          value = s.getString("extra_displaynumber_value"),
          percent = s.getInt("extra_displaynumber_percent"));
    }

    fun fromFloat(number: Float): DisplayNumber {
      val negative = number < 0
      val numberAsString = number.toString()
      val value = if (negative) numberAsString.drop(1) else numberAsString
      return DisplayNumber(negative, value, 0)
    }
  }
}

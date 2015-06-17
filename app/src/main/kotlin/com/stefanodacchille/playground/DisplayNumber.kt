package com.stefanodacchille.playground

import android.os.Bundle

data class DisplayNumber(val negative: Boolean, val value: String, val percent: Int) {

  fun toDisplay(): String {
    var displayValue = value.toFloat()
    if (this.negative) displayValue = value.toFloat().minus()
    for (i in 0..this.percent) {
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

    val zero = DisplayNumber(negative = false, value = "", percent = 0)

    fun fromBundle(s: Bundle?): DisplayNumber {
      if (s == null) return zero

      return DisplayNumber(negative = s.getBoolean("extra_displaynumber_negative"),
          value = s.getString("extra_displaynumber_value"),
          percent = s.getInt("extra_displaynumber_percent"));
    }

    fun toBundle(dn: DisplayNumber): Bundle {
      var b = Bundle()
      b.putBoolean("extra_displaynumber_negative", dn.negative)
      b.putString("extra_displaynumber_value", dn.value)
      b.putInt("extra_displaynumber_percent", dn.percent)
      return b
    }


    fun fromFloat(number: Float): DisplayNumber {
      val negative = number > 0
      val numberAsString = number.toString()
      val value = if (negative) numberAsString.drop(1) else numberAsString
      return DisplayNumber(negative, value, 0)
    }
  }
}
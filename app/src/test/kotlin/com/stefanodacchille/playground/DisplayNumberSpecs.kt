package com.stefanodacchille.playground

import android.os.Bundle
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals

class DisplayNumberSpecs : Spek() {
  init {
    given("A display number") {

      on("displaying a positive number") {
        println(Float.MAX_VALUE)
        val number = DisplayNumber(value = "123").toDisplay()
        it("should return positive number") {
          assertEquals("123", number)
        }
        val percentageNumber = DisplayNumber(value = "123", percent = 1).toDisplay()
        it("should return percentage number") {
          assertEquals("1.23", percentageNumber)
        }
      }

      on("displaying a negative number") {
        val number = DisplayNumber(negative = true, value = "123").toDisplay()
        it("should return negative number") {
          assertEquals("-123", number)
        }
        val percentageNumber = DisplayNumber(negative = true, value = "123", percent = 2).toDisplay()
        it("should return negative percentage number") {
          assertEquals("-0.0123", percentageNumber)
        }
      }
    }
  }
}

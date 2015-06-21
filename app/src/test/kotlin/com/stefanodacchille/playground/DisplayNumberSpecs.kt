package com.stefanodacchille.playground

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals

class DisplayNumberSpecs : Spek() {

  init {
    given("a display number") {

      on("representing a positive number") {
        val number = DisplayNumber(value = "123").toDisplay()
        it("should display positive number") {
          assertEquals("123", number)
        }
        val percentageNumber = DisplayNumber(value = "123", percent = 1).toDisplay()
        it("should display a positive decimal number") {
          assertEquals("1.23", percentageNumber)
        }
      }

      on("representing zero") {
        it("should display zero") {
          assertEquals("0", DisplayNumber.zero.toDisplay())
        }
      }

      on("representing a negative number") {
        val number = DisplayNumber(negative = true, value = "123").toDisplay()
        it("should display negative number") {
          assertEquals("-123", number)
        }
        val percentageNumber = DisplayNumber(negative = true, value = "123", percent = 2).toDisplay()
        it("should display a negative decimal number") {
          assertEquals("-0.0123", percentageNumber)
        }
      }

      on("representing not a number") {
        it("should display an error message") {
          assertEquals("Not a number", DisplayNumber.fromDecimal(Double.NaN).toDisplay())
        }
      }

      on("representing infinity") {
        it("should display an error message") {
          val errorMessage = "You asked me too much"
          assertEquals(errorMessage,
              DisplayNumber.fromDecimal(Double.POSITIVE_INFINITY).toDisplay())
          assertEquals(errorMessage,
              DisplayNumber.fromDecimal(Double.NEGATIVE_INFINITY).toDisplay())
        }
      }
    }
  }
}

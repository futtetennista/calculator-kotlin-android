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
        val displayNumber = DisplayNumber(value = "123", percent = 1)
        val percentageNumber = displayNumber.toDisplay()
        it("should display a positive decimal number") {
          assertEquals("1.23", percentageNumber)
        }
        it("should create decimal number correctly") {
          assertEquals(1.23, displayNumber.toDecimal())
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

        val displayNumber = DisplayNumber(negative = true, value = "123", percent = 2)
        val percentageNumber = displayNumber.toDisplay()
        it("should display a negative decimal number") {
          assertEquals("-0.0123", percentageNumber)
        }
        it("should create decimal number correctly") {
          assertEquals(-0.0123, displayNumber.toDecimal())
        }
      }

      on("representing not a number") {
        it("should display an error message") {
          assertEquals("Not a number", DisplayNumber.fromDecimal(Double.NaN).toDisplay())
        }
        it("should just return not a number value") {
          assertEquals(Double.NaN, DisplayNumber.fromDecimal(Double.NaN).toDecimal())
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
        it("should just return infinity value") {
          assertEquals(Double.POSITIVE_INFINITY,
              DisplayNumber.fromDecimal(Double.POSITIVE_INFINITY).toDecimal())
          assertEquals(Double.NEGATIVE_INFINITY,
              DisplayNumber.fromDecimal(Double.NEGATIVE_INFINITY).toDecimal())
        }
      }
    }
  }
}

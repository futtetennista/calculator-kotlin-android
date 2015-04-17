package com.stefanodacchille.playground

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import kotlinx.android.anko.*
import playground.stefanodacchille.com.calculator.R

public class Calculator : Activity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    gridLayout {
      orientation = GridLayout.HORIZONTAL
      columnCount = 4

      display().layoutParams {
        rowSpec = GridLayout.spec(0, 2)
        columnSpec = GridLayout.spec(0, 4)
        setGravity(Gravity.FILL_HORIZONTAL)
      }

      topView("C", 0)
      topView("±", 1)
      topView("%", 2)
      rightView("/", 3)

      numberView("7", 0)
      numberView("8", 1)
      numberView("9", 2)
      rightView("x", 3)

      numberView("6", 0)
      numberView("5", 1)
      numberView("4", 2)
      rightView("-", 3)

      numberView("1", 0)
      numberView("2", 1)
      numberView("3", 2)
      rightView("+", 3)

      button("0"){
        gravity = Gravity.CENTER
        background = getResources().getDrawable(R.drawable.border)
      }.layoutParams {
        columnSpec = GridLayout.spec(0, 2, 1f)
        setGravity(Gravity.FILL_HORIZONTAL)
      }
      numberView(",", 2)
      rightView("=", 3)
    }
  }

  fun _GridLayout.topView(text: String, column: Int): Button? {
    return buttonView(text, android.R.drawable.btn_default, column)
  }

  fun _GridLayout.rightView(text: String, column: Int): Button {
    return buttonView(text, android.R.drawable.btn_default, column)
  }

  fun _GridLayout.numberView(text: String, column: Int): Button {
    return buttonView(text, android.R.drawable.btn_default, column)
  }

  fun _GridLayout.buttonView(text: String, bgRes: Int, column: Int): Button {
    return button(text){
      background = getResources().getDrawable(R.drawable.border)
    }.layoutParams {
      columnSpec = GridLayout.spec(column, 1f)
      setGravity(Gravity.FILL_HORIZONTAL)
    }
  }

  fun _GridLayout.display(): TextView {
    return textView("0") {
      backgroundColor = getResources().getColor(android.R.color.black)
      gravity = Gravity.BOTTOM
      textColor = getResources().getColor(android.R.color.white)
      textSize = 32f
    }
  }

  open class Action private() {
    class Digit(value : String) : Action()
    class Add() : Action()
    class Sub() : Action()
    class Mul() : Action()
    class Div() : Action()
    class Percent() : Action()
    class Negate() : Action()
    class Decimal() : Action()
    class Equals() : Action()
  }

  open class State private() {
    val nan = Error("Not a number")

    class Init(value : CalcNumber) : State()
    class Operation(left : Float, f : (Float, Float) -> Float, n : Number) : State()
    class Error(msg: String) : State()
  }

  class CalcNumber(negative : Boolean, value : String, percent : Int) {
    val zero = CalcNumber(negative=false, value="", percent=0)
  }
}

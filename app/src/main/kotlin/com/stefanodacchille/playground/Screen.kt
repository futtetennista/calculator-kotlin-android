package com.stefanodacchille.playground

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import org.jetbrains.anko.*
import playground.stefanodacchille.com.calculator.R
import java.lang
import javax.inject.Inject

public class Screen : Activity() {

    Inject var presenter : Presenter? = null
  // OR
  //  var presenter : Presenter? = null
  //    [Inject] set

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val text: DisplayNumber = DisplayNumber.fromBundle(savedInstanceState)

    gridLayout {
      orientation = GridLayout.HORIZONTAL
      columnCount = 4

      display(text).layoutParams {
        rowSpec = GridLayout.spec(0, 2)
        columnSpec = GridLayout.spec(0, 4)
        setGravity(Gravity.FILL_HORIZONTAL)
      }

      topView("C", 0, Action.CLEAR)
      topView("±", 1, Action.NEGATE)
      topView("%", 2, Action.PERCENT)
      rightView("/", 3, Action.DIV)

      numberView("7", 0, Action.ZERO)
      numberView("8", 1, Action.ONE)
      numberView("9", 2, Action.TWO)
      rightView("x", 3, Action.MUL)

      numberView("6", 0, Action.SIX)
      numberView("5", 1, Action.FIVE)
      numberView("4", 2, Action.FOUR)
      rightView("-", 3, Action.SUB)

      numberView("1", 0, Action.ONE)
      numberView("2", 1, Action.TWO)
      numberView("3", 2, Action.THREE)
      rightView("+", 3, Action.ADD)

      numberZeroView()
      numberView(",", 2, Action.DECIMAL)
      rightView("=", 3, Action.EQUALS)
    }
  }

  private fun _GridLayout.numberZeroView() {
    button("0") {
      gravity = Gravity.CENTER
      background = getResources().getDrawable(R.drawable.border)
      onClick { }
    }.layoutParams {
      columnSpec = GridLayout.spec(0, 2, 1f)
      setGravity(Gravity.FILL_HORIZONTAL)
    }
  }

  private fun _GridLayout.topView(text: String, column: Int, action: Action): Button? {
    return buttonView(text, android.R.drawable.btn_default, column, action)
  }

  private fun _GridLayout.rightView(text: String, column: Int, action: Action): Button {
    return buttonView(text, android.R.drawable.btn_default, column, action)
  }

  private fun _GridLayout.numberView(text: String, column: Int, action: Action): Button {
    return buttonView(text, android.R.drawable.btn_default, column, action)
  }

  private fun _GridLayout.buttonView(text: String, bgRes: Int, column: Int, action: Action) : Button {
    return button(text) {
      background = getResources().getDrawable(R.drawable.border)
      onClick { presenter?.update(action) }
    }.layoutParams {
      columnSpec = GridLayout.spec(column, 1f)
      setGravity(Gravity.FILL_HORIZONTAL)
    }
  }

  private fun _GridLayout.display(text: DisplayNumber): TextView {
    return textView(text.toDisplay()) {
      id = R.id.display
      backgroundColor = getResources().getColor(android.R.color.black)
      gravity = Gravity.BOTTOM
      textColor = getResources().getColor(android.R.color.white)
      textSize = 32f
    }
  }

  fun updateDisplay(state: State) {
    val display = find<TextView>(R.id.display)
    display.text = when (state) {
      is State.Init -> state.displayNumber.toDisplay()
      is State.Operation -> state.displayNumber.toDisplay()
      is State.Error -> state.msg
      else -> ""
    }
  }

  // PRESENTER
  class Presenter {

    var state: State = State.Init(DisplayNumber.zero)

    fun update(action: Action) {
      this.state = when (action) {
        is Action.ADD, is Action.DIV, is Action.MUL, is Action.SUB ->
          applyBinaryOperation(action, state)
        is Action.EQUALS -> applyEquals(action, state)
        is Action.DECIMAL -> applyDecimal(state)
        is Action.NEGATE -> applyNegate(state)
        is Action.PERCENT -> applyPercent(state)
        is Action.CLEAR -> State.Init(DisplayNumber.zero)
        else -> applyDigit(action, state)
      }
      // TODO: update ui
    }

    private fun applyDigit(action: Action, state: State): State {
      when (state) {
        is State.Init -> {
          val newValue = state.displayNumber.value + action.ordinal().toString()
          return state.copy(displayNumber = state.displayNumber.copy(value = newValue))
        }
        is State.Operation -> {
          val newValue = state.displayNumber.value + action.ordinal().toString()
          return state.copy(displayNumber = state.displayNumber.copy(value = newValue))
        }
        is State.Error ->
          return State.Init(displayNumber = DisplayNumber.fromFloat(action.ordinal().toFloat()))
        else -> throw AssertionError("Unexpected state $state")
      }
    }

    private fun applyPercent(state: State): State {
      when (state) {
        is State.Init -> {
          val newPercent = state.displayNumber.percent + 1
          return state.copy(displayNumber = state.displayNumber.copy(percent = newPercent))
        }
        is State.Operation -> {
          val newPercent = state.displayNumber.percent + 1
          return state.copy(displayNumber = state.displayNumber.copy(percent = newPercent))
        }
        is State.Error -> return state
        else -> throw AssertionError("Unexpected state $state")
      }
    }

    private fun applyBinaryOperation(a: Action, state: State): State {
      when (state) {
        is State.Init ->
          return State.Operation(state.displayNumber.toFloat(), a, DisplayNumber.zero)
        is State.Operation -> {
          try {
            val result = applyBinaryOperation(state.left, a, state.displayNumber.toFloat())
            return State.Operation(result, a, DisplayNumber.zero)
          } catch (e : ArithmeticException) {
            return State.nan
          }
        }
        is State.Error -> return state
        else -> throw AssertionError("Unexpected state $state")
      }
    }

    private fun applyEquals(a: Action, state: State): State {
      when (state) {
        is State.Operation -> {
          val result = applyBinaryOperation(state.left, a, state.displayNumber.toFloat())
          return State.Operation(result, a, DisplayNumber.fromFloat(result))
        }
        else -> return state
      }
    }

    private fun applyNegate(state: State): State {
      when (state) {
        is State.Init ->
          return State.Init(state.displayNumber.copy(negative = true))
        is State.Operation -> {
          val displayNumber = state.displayNumber.copy(negative = true)
          return state.copy(left = state.left, f = state.f, displayNumber = displayNumber)
        }
        is State.Error -> return state
        else -> throw AssertionError("Unexpected state $state")
      }
    }

    private fun applyDecimal(state: State): State {
      fun isDecimal(n: DisplayNumber): Boolean {
        return n.value.endsWith('.')
      }

      fun toDecimal(n: DisplayNumber): DisplayNumber {
        if (isDecimal(n)) return n else return n.copy(value = n.value + ".")
      }

      when (state) {
        is State.Init ->
          return state.copy(toDecimal(state.displayNumber))
        is State.Operation -> {
          val displayNumber = toDecimal(state.displayNumber)
          return state.copy(left = state.left, f = state.f, displayNumber = displayNumber)
        }
        is State.Error -> return state
        else -> throw AssertionError("Unexpected state $state")
      }
    }

    private fun applyBinaryOperation(left: Float, action: Action, right: Float) : Float {
      return toBinaryOperation(action) (left, right)
    }

    private fun toBinaryOperation(action: Action): (Float, Float) -> Float {
      when (action) {
        is Action.ADD -> return { x: Float, y: Float -> x + y }
        is Action.SUB -> return { x: Float, y: Float -> x - y }
        is Action.DIV -> return { x: Float, y: Float -> x / y }
        is Action.MUL -> return { x: Float, y: Float -> x * y }
        else -> throw AssertionError("Unknown binary action ${action.name()}")
      }
    }
  }

  // MODEL
  // Pity kotlin doesn't have ADT
  enum class Action {
    // digits
    ZERO, ONE, TWO THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE,
    // binary operations
    ADD, SUB, MUL, DIV,
    // unary operations
    PERCENT, NEGATE, DECIMAL,
    EQUALS, CLEAR
  }

  open class State private () {
    data class Init(val displayNumber: DisplayNumber) : State()
    data class Operation(val left: Float, val f: Action, val displayNumber: DisplayNumber) : State()
    data class Error(val msg: String) : State()

    companion object {
      val nan = State.Error("Not a number")
    }
  }
}

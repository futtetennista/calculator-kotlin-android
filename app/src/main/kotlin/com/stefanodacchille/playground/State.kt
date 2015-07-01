package com.stefanodacchille.playground

open class State() {

  data class Init(val displayNumber: DisplayNumber) : State()
  data class Operation(val left: Double,
                       val action: Action,
                       val displayNumber: DisplayNumber,
                       val lastRightOperand : Double = 0.0) : State()
  data class Error(val msg: String) : State()

  companion object {
    val nan = State.Error("Not a number")

    val initialState: State = State.Init(DisplayNumber.zero)
  }
}

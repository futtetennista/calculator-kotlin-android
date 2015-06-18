package com.stefanodacchille.playground

import rx.Observable
import rx.functions.Func2
import rx.lang.kotlin.PublishSubject
import rx.subjects.Subject

public class ReactiveModel {

  companion object {

    val actionSubject = PublishSubject<Action>()

    val updateObservable = actionSubject.scan(State.initialState, { state, action ->
      when (action) {
        Action.ADD, Action.DIV, Action.MUL, Action.SUB -> applyBinaryOperation(action, state)
        Action.EQUALS -> applyEquals(state)
        Action.DECIMAL -> applyDecimal(state)
        Action.NEGATE -> applyNegate(state)
        Action.PERCENT -> applyPercent(state)
        Action.CLEAR -> State.initialState
        else -> applyDigit(action, state)
      }
    })

    private fun applyDigit(action: Action, state: State): State {
      when (state) {
        is State.Init -> {
          val newValue = if (state.displayNumber.value == "0") {
            action.ordinal().toString()
          } else {
            state.displayNumber.value + action.ordinal().toString()
          }
          return state.copy(displayNumber = state.displayNumber.copy(value = newValue))
        }
        is State.Operation -> {
          val newValue = if (state.displayNumber == DisplayNumber.zero) {
            action.ordinal().toString()
          } else {
            state.displayNumber.value + action.ordinal().toString()
          }
          return state.copy(displayNumber = state.displayNumber.copy(value = newValue))
        }
        is State.Error ->
          return State.Init(displayNumber = DisplayNumber.fromFloat(action.ordinal().toFloat()))
        else -> throw AssertionError("Unknown state $state")
      }
    }

    private fun applyPercent(state: State?): State {
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
            return state.copy(action = a, displayNumber = DisplayNumber.zero)
          } catch (e: ArithmeticException) {
            return State.nan
          }
        }
        is State.Error -> return state
        else -> throw AssertionError("Unexpected state $state")
      }
    }

    private fun applyEquals(state: State): State {
      when (state) {
        is State.Operation -> {
          val result = applyBinaryOperation(state.left, state.action, state.displayNumber.toFloat())
          return state.copy(left = result, displayNumber = DisplayNumber.zero)
        }
        else -> return state
      }
    }

    private fun applyNegate(state: State?): State {
      when (state) {
        is State.Init ->
          return State.Init(state.displayNumber.copy(negative = true))
        is State.Operation -> {
          val displayNumber = state.displayNumber.copy(negative = true)
          return state.copy(left = state.left, action = state.action, displayNumber = displayNumber)
        }
        is State.Error -> return state
        else -> throw AssertionError("Unexpected state $state")
      }
    }

    private fun applyDecimal(state: State?): State {
      fun isDecimal(n: DisplayNumber): Boolean {
        return n.value.contains('.')
      }

      fun toDecimal(n: DisplayNumber): DisplayNumber {
        if (isDecimal(n)) return n else return n.copy(value = n.value + ".")
      }

      when (state) {
        is State.Init ->
          return state.copy(toDecimal(state.displayNumber))
        is State.Operation -> {
          val displayNumber = toDecimal(state.displayNumber)
          return state.copy(left = state.left, action = state.action, displayNumber = displayNumber)
        }
        is State.Error -> return state
        else -> throw AssertionError("Unexpected state $state")
      }
    }

    private fun applyBinaryOperation(left: Float, action: Action, right: Float): Float {
      return toBinaryOperation(action) (left, right)
    }

    private fun toBinaryOperation(action: Action): (Float, Float) -> Float {
      when (action) {
        Action.ADD -> return { x: Float, y: Float -> x + y }
        Action.SUB -> return { x: Float, y: Float -> x - y }
        Action.DIV -> return { x: Float, y: Float -> x / y }
        Action.MUL -> return { x: Float, y: Float -> x * y }
        else -> throw AssertionError("Unknown binary action ${action.name()}")
      }
    }
  }
}
package com.stefanodacchille.playground

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals
import kotlin.test.fail

class ReactiveModelSpecs : Spek() {
  init {
    given("a subscription to state updates is made") {
      var currentState: State = State.initialState
      ReactiveModel.updateObservable.subscribe { state -> currentState = state }

      // Doesn't do what I expect
      //      beforeOn {
      //        ReactiveModel.actionSubject.onNext(Action.CLEAR)
      //      }

      on("entering digits") {
        it("should save the entered number correctly") {
          //          ReactiveModel.actionSubject.onNext(Action.ZERO)
          //          when (currentState) {
          //            is State.Init -> {
          //              assertEquals("0", currentState.displayNumber.toDisplay())
          //            }
          //          }
          ReactiveModel.actionSubject.onNext(Action.ZERO)
          assertEquals("0", (currentState as State.Init).displayNumber.toDisplay())
          Action.digits().foldRight("") { action, displayNumber ->
            ReactiveModel.actionSubject.onNext(action)
            val newDisplayNumber = displayNumber + action.ordinal().toString()
            // Better way to do this!?
            assertEquals(newDisplayNumber, (currentState as State.Init).displayNumber.toDisplay())
            newDisplayNumber
          }
        }
      }

      // TODO: Test this using https://github.com/pholser/junit-quickcheck
      on("applying a binary operation") {

        it("should save state correctly") {
          ReactiveModel.actionSubject.onNext(Action.CLEAR)
          ReactiveModel.actionSubject.onNext(Action.SIX)
          ReactiveModel.actionSubject.onNext(Action.ADD)
          var expectedState: State = State.Operation(6.0, Action.ADD, DisplayNumber.zero)
          assertEquals(expectedState, currentState)

          ReactiveModel.actionSubject.onNext(Action.THREE)
          expectedState = State.Operation(6.0, Action.ADD, DisplayNumber(value = "3"))
          assertEquals(expectedState, currentState)
        }
      }

      on("adding two numbers") {
        it("should calculate the result correctly") {
          ReactiveModel.actionSubject.onNext(Action.CLEAR)
          ReactiveModel.actionSubject.onNext(Action.SIX)
          ReactiveModel.actionSubject.onNext(Action.ADD)
          ReactiveModel.actionSubject.onNext(Action.THREE)
          ReactiveModel.actionSubject.onNext(Action.EQUALS)

          var expectedState = State.Operation(9.0, Action.ADD, DisplayNumber.zero)
          assertEquals(expectedState, currentState)
        }
      }

      on("subtracting two numbers") {
        it("should calculate the result correctly") {
          ReactiveModel.actionSubject.onNext(Action.CLEAR)
          ReactiveModel.actionSubject.onNext(Action.THREE)
          ReactiveModel.actionSubject.onNext(Action.SUB)
          ReactiveModel.actionSubject.onNext(Action.SIX)
          ReactiveModel.actionSubject.onNext(Action.EQUALS)

          val expectedState = State.Operation(-3.0, Action.SUB, DisplayNumber.zero)
          assertEquals(expectedState, currentState)
        }
      }

      on("multiplying two numbers") {
        it("should calculate the result correctly") {
          ReactiveModel.actionSubject.onNext(Action.CLEAR)
          ReactiveModel.actionSubject.onNext(Action.SEVEN)
          ReactiveModel.actionSubject.onNext(Action.MUL)
          ReactiveModel.actionSubject.onNext(Action.SIX)
          ReactiveModel.actionSubject.onNext(Action.EQUALS)

          val expectedState = State.Operation(42.0, Action.MUL, DisplayNumber.zero)
          assertEquals(expectedState, currentState)
        }
      }

      on("dividing two numbers") {
        it("should calculate the result correctly") {
          ReactiveModel.actionSubject.onNext(Action.CLEAR)
          ReactiveModel.actionSubject.onNext(Action.SIX)
          ReactiveModel.actionSubject.onNext(Action.DIV)
          ReactiveModel.actionSubject.onNext(Action.FIVE)
          ReactiveModel.actionSubject.onNext(Action.EQUALS)

          val expectedState = State.Operation(1.2, Action.DIV, DisplayNumber.zero)
          assertEquals(expectedState, currentState)
        }
      }

      on("applying an unary operation") {
        it("should negate number") {
          ReactiveModel.actionSubject.onNext(Action.CLEAR)
          ReactiveModel.actionSubject.onNext(Action.SIX)
          ReactiveModel.actionSubject.onNext(Action.NEGATE)

          var expectedState: State = State.Init(DisplayNumber(negative = true, value = "6"))
          assertEquals(expectedState, currentState)

          ReactiveModel.actionSubject.onNext(Action.NEGATE)

          expectedState = State.Init(DisplayNumber(value = "6"))
          assertEquals(expectedState, currentState)

          ReactiveModel.actionSubject.onNext(Action.ADD)
          ReactiveModel.actionSubject.onNext(Action.SEVEN)
          ReactiveModel.actionSubject.onNext(Action.NEGATE)

          expectedState =
              State.Operation(6.0, Action.ADD, DisplayNumber(negative = true, value = "7"))
          assertEquals(expectedState, currentState)
        }
      }

      on("applying the dot operator") {
        it("should create decimal number") {
          ReactiveModel.actionSubject.onNext(Action.CLEAR)
          ReactiveModel.actionSubject.onNext(Action.SIX)
          ReactiveModel.actionSubject.onNext(Action.DECIMAL)
          ReactiveModel.actionSubject.onNext(Action.SIX)

          var expectedState : State = State.Init(DisplayNumber(value = "6.6"))
          assertEquals(expectedState, currentState)

          ReactiveModel.actionSubject.onNext(Action.MUL)
          ReactiveModel.actionSubject.onNext(Action.ONE)
          ReactiveModel.actionSubject.onNext(Action.DECIMAL)
          ReactiveModel.actionSubject.onNext(Action.TWO)

          expectedState : State = State.Operation(6.6, Action.MUL, DisplayNumber(value = "1.2"))
          assertEquals(expectedState, currentState)
        }

        it("should not do anything if number is already decimal") {
          ReactiveModel.actionSubject.onNext(Action.CLEAR)
          ReactiveModel.actionSubject.onNext(Action.SIX)
          ReactiveModel.actionSubject.onNext(Action.DECIMAL)
          ReactiveModel.actionSubject.onNext(Action.DECIMAL)
          ReactiveModel.actionSubject.onNext(Action.SIX)

          var expectedState : State = State.Init(DisplayNumber(value = "6.6"))
          assertEquals(expectedState, currentState)

          ReactiveModel.actionSubject.onNext(Action.DECIMAL)
          ReactiveModel.actionSubject.onNext(Action.SIX)

          expectedState : State = State.Init(DisplayNumber(value = "6.66"))
          assertEquals(expectedState, currentState)
        }
      }

      on("applying a percentage") {
        it("should apply percentage to number") {
          ReactiveModel.actionSubject.onNext(Action.CLEAR)
          ReactiveModel.actionSubject.onNext(Action.SIX)
          ReactiveModel.actionSubject.onNext(Action.PERCENT)

          var expectedState : State = State.Init(DisplayNumber(value = "6", percent = 1))
          assertEquals(expectedState, currentState)

          ReactiveModel.actionSubject.onNext(Action.ADD)
          ReactiveModel.actionSubject.onNext(Action.NINE)
          ReactiveModel.actionSubject.onNext(Action.PERCENT)
          ReactiveModel.actionSubject.onNext(Action.PERCENT)

          expectedState =
              State.Operation(0.06, Action.ADD, DisplayNumber(value = "9", percent = 2))
          assertEquals(expectedState, currentState)
        }
      }

      on("applying an operation after a previous operation") {
        it("should override the last operation with the most recent one") {
          fail("Not implemented")
        }
      }

      on("getting the result of an operation") {
        it("should have correct operation state") {
          fail("Not implemented")
        }
      }

      on("getting the result of an operation multiple times") {
        it("should apply the given operations multiple times") {
          fail("Not implemented")
        }
      }

      on("clearing the current state") {
        it("should have a zero initial state") {
          ReactiveModel.actionSubject.onNext(Action.SIX)
          ReactiveModel.actionSubject.onNext(Action.CLEAR)
          assertEquals(State.initialState, currentState)
        }
      }
    }
  }
}
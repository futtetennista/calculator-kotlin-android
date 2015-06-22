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
        var expectedState: State

        it("should save state correctly") {
          ReactiveModel.actionSubject.onNext(Action.CLEAR)
          ReactiveModel.actionSubject.onNext(Action.SIX)
          ReactiveModel.actionSubject.onNext(Action.ADD)
          expectedState = State.Operation(6.0, Action.ADD, DisplayNumber.zero)
          assertEquals(expectedState, currentState)

          ReactiveModel.actionSubject.onNext(Action.THREE)
          expectedState = State.Operation(6.0, Action.ADD, DisplayNumber(value = "3"))
          assertEquals(expectedState, currentState)
        }

        it("should calculate the result of an addition correctly") {
          ReactiveModel.actionSubject.onNext(Action.CLEAR)
          ReactiveModel.actionSubject.onNext(Action.SIX)
          ReactiveModel.actionSubject.onNext(Action.ADD)
          ReactiveModel.actionSubject.onNext(Action.THREE)
          ReactiveModel.actionSubject.onNext(Action.EQUALS)

          expectedState = State.Operation(9.0, Action.ADD, DisplayNumber.zero)
          assertEquals(expectedState, currentState)
        }

        it("should calculate the result of a subtraction correctly") {
          ReactiveModel.actionSubject.onNext(Action.CLEAR)
          ReactiveModel.actionSubject.onNext(Action.THREE)
          ReactiveModel.actionSubject.onNext(Action.SUB)
          ReactiveModel.actionSubject.onNext(Action.SIX)
          ReactiveModel.actionSubject.onNext(Action.EQUALS)

          expectedState = State.Operation(-3.0, Action.SUB, DisplayNumber.zero)
          assertEquals(expectedState, currentState)
        }

        it("should calculate the result of a multiplication correctly") {
          ReactiveModel.actionSubject.onNext(Action.CLEAR)
          ReactiveModel.actionSubject.onNext(Action.SEVEN)
          ReactiveModel.actionSubject.onNext(Action.MUL)
          ReactiveModel.actionSubject.onNext(Action.SIX)
          ReactiveModel.actionSubject.onNext(Action.EQUALS)

          expectedState = State.Operation(42.0, Action.MUL, DisplayNumber.zero)
          assertEquals(expectedState, currentState)
        }

        it("should calculate the result of a division correctly") {
          ReactiveModel.actionSubject.onNext(Action.CLEAR)
          ReactiveModel.actionSubject.onNext(Action.SIX)
          ReactiveModel.actionSubject.onNext(Action.DIV)
          ReactiveModel.actionSubject.onNext(Action.FIVE)
          ReactiveModel.actionSubject.onNext(Action.EQUALS)

          expectedState = State.Operation(1.2, Action.DIV, DisplayNumber.zero)
          assertEquals(expectedState, currentState)
        }
      }

      on("updating the state by applying an unary operation") {
        it("should have correct operation state") {
          fail("Not implemented")
        }
      }

      on("updating the state by applying an operation after a previous operation") {
        it("should have correct operation state") {
          fail("Not implemented")
        }
      }

      on("updating the state by getting the result of an operation") {
        it("should have correct operation state") {
          fail("Not implemented")
        }
      }

      on("updating the state by getting the result of an operation multiple times") {
        it("should have correct operation state") {
          fail("Not implemented")
        }
      }

      on("updating the state by applying the decimal operator on init state") {
        it("should have correct init state") {
          fail("Not implemented")
        }
      }

      on("updating the state by applying the decimal operator on operation state") {
        it("should have correct operation state") {
          fail("Not implemented")
        }
      }

      on("updating the state by clearing it") {
        it("should have a zero init state") {
          ReactiveModel.actionSubject.onNext(Action.SIX)
          ReactiveModel.actionSubject.onNext(Action.CLEAR)
          assertEquals(State.initialState, currentState)
        }
      }
    }
  }
}
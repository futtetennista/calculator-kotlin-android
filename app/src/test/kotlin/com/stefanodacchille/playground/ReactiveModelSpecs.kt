package com.stefanodacchille.playground

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals
import kotlin.test.fail

class ReactiveModelSpecs : Spek() {
  init {
    given("a subscription to state updates is made") {
      var currentState: State = State.initialState
      ReactiveModel.updateObservable.subscribe { state -> currentState = state }

      on("updating the state by adding digits") {
        it("should have correct init state") {
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

      on("updating the state by applying a binary operation") {
        it("should have correct operation state") {
          fail("Not implemented")
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
          fail("Not implemented")
        }
      }
    }
  }
}
package com.stefanodacchille.playground

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Gravity
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import org.jetbrains.anko.*
import playground.stefanodacchille.com.calculator.R
import rx.Observable
import rx.Subscription
import rx.lang.kotlin.PublishSubject
import rx.subjects.PublishSubject
import rx.subjects.Subject
import java.lang

public class Screen : Activity() {

  private var subscription: Subscription? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val text: DisplayNumber = DisplayNumber.fromBundle(savedInstanceState)

    buildLayout(text)

    subscription = ReactiveModel.updateObservable.subscribe { state -> updateDisplay(state) }
  }

  override fun finish() {
    subscription?.unsubscribe()
    super.finish()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    //    outState.putBundle("", presenter.state)
  }

  override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
    super.onSaveInstanceState(outState, outPersistentState)
  }

  private fun buildLayout(text: DisplayNumber) {
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

      numberView("7", 0, Action.SEVEN)
      numberView("8", 1, Action.EIGHT)
      numberView("9", 2, Action.NINE)
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
      onClick {
        ReactiveModel.actionSubject.onNext(Action.ZERO)
      }
    }.layoutParams {
      columnSpec = GridLayout.spec(0, 2, 1f)
      setGravity(Gravity.FILL_HORIZONTAL)
    }
  }

  private fun _GridLayout.topView(text: String, column: Int, action: Action): Button? {
    return buttonView(text, R.drawable.border, column, action)
  }

  private fun _GridLayout.rightView(text: String, column: Int, action: Action): Button {
    return buttonView(text, R.drawable.border, column, action)
  }

  private fun _GridLayout.numberView(text: String, column: Int, action: Action): Button {
    return buttonView(text, R.drawable.border, column, action)
  }

  private fun _GridLayout.buttonView(text: String, bgResId: Int, column: Int, action: Action): Button {
    return button(text) {
      background = getResources().getDrawable(bgResId)
      onClick {
        ReactiveModel.actionSubject.onNext(action)
      }
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

  private fun updateDisplay(state: State) {
    val display = find<TextView>(R.id.display)
    display.text = when (state) {
      is State.Init -> state.displayNumber.toDisplay()
      is State.Operation -> {
        if (state.displayNumber == DisplayNumber.zero) {
          state.left.toString()
        } else {
          state.displayNumber.toDisplay()
        }
      }
      is State.Error -> state.msg
      else -> ":-("
    }
  }
}

package com.gmail.serhiiromanchuk.mycalculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private var expression = ""
    private val _expressionLiveData = MutableLiveData<String>()
    val expressionLiveData: LiveData<String>
        get() = _expressionLiveData

    fun addToExpression(str: String) {

        when(str) {
            "AC" -> expression = ""
            "CLEAR" -> clearLastSymbol()
            "+", "–", "×", "÷", "%" -> checkLastSymbol(str)
            else -> expression += str
        }
        _expressionLiveData.value = expression
    }

    private fun checkLastSymbol(mathSymbol: String) {
        val mathSymbolList = listOf('+', '–', '×', '÷', '%')
        mathSymbolList.forEach {
            if (expression[expression.length-1] == it) {
                clearLastSymbol()
            }
        }
        expression += mathSymbol
    }

    private fun clearLastSymbol() {
        expression = expression.substring(0, expression.length - 1)
    }
}
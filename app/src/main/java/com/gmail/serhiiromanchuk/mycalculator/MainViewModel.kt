package com.gmail.serhiiromanchuk.mycalculator

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.script.ScriptEngineManager
import javax.script.ScriptException
import kotlin.math.exp
import kotlin.math.floor

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var expression = ""
    private var result = 0.0
    private val mathSymbolList = listOf('+', '-', '*', '/', '%')

    private val _expressionLiveData = MutableLiveData<String>()
    val expressionLiveData: LiveData<String>
        get() = _expressionLiveData
    private val _resultLiveData = MutableLiveData<String>()
    val resultLiveData: LiveData<String>
        get() = _resultLiveData

    fun addToExpression(str: String) {
        when (str) {
            "AC" -> {
                expression = ""
                _resultLiveData.value = "0"
            }
            "CLEAR" -> clearLastSymbol()
            "%" -> calculatePercentage()
            "+", "-", "*", "/" -> checkLastSymbol(str)
            else -> expression += str
        }
        _expressionLiveData.value = expression

        if (isLastCharIsNumber() && expression.isNotBlank()) {
            result = resultOfExpression(expression)
            updateResult(result)
        }
    }

    private fun resultOfExpression(expression: String): Double {
        val engine = ScriptEngineManager().getEngineByName("rhino")

        return try {
            engine.eval(expression) as Double
        } catch (e: ScriptException) {
            Toast.makeText(getApplication(), "Invalid input", Toast.LENGTH_SHORT).show()
            0.0
        }
    }

    private fun updateResult(result: Double) {
        if (result.isInteger()) {
            _resultLiveData.value = "= ${result.toInt()}"
        } else _resultLiveData.value = "= $result"
    }

    private fun calculatePercentage() {
        val numbersList: MutableList<String> = expression.split("+", "*", "/", "-").toMutableList()
        val lastNumber = numbersList[numbersList.size - 1]

        //Remove the last number from the expression
        expression = expression.substring(0, expression.lastIndexOf(lastNumber))

        when (expression[expression.lastIndex]) {
            '/', '*' -> {
                expression += lastNumber.toDouble() / 100
            }
            '+', '-' -> {
                val percentValue = resultOfExpression(
                    expression.substring(
                        0,
                        expression.lastIndex
                    )
                ) * (lastNumber.toDouble() / 100)

                expression += if (percentValue.isInteger()) {
                    percentValue.toInt()
                } else percentValue
            }
        }
    }

    /**
     * The method checks for duplication of mathematical signs at the end of the expression,
     * and if duplication is found, it overwrites with the last entered sign.
     */
    private fun checkLastSymbol(mathSymbol: String) {
        if (!isLastCharIsNumber()) {
            clearLastSymbol()
        }
        expression += mathSymbol
    }

    /**
     * Return true if last char in expression is number
     */
    private fun isLastCharIsNumber(): Boolean {
        if (expression.isNotBlank()) {
            mathSymbolList.forEach {
                if (expression[expression.length - 1] == it) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * Clear last symbol at the expression
     */
    private fun clearLastSymbol() {
        if (expression.isNotBlank()) {
            expression = expression.substring(0, expression.length - 1)
        }
    }

    /**
     * Return true if double is integer
     */
    private fun Double.isInteger() = (this == floor(this)) && !this.isInfinite()
}
package com.gmail.serhiiromanchuk.mycalculator

import javax.script.ScriptEngineManager
import javax.script.ScriptException
import kotlin.math.floor

/**
 * Return true if double is integer
 */
fun Double.isInteger() = (this == floor(this)) && !this.isInfinite()

class Expression(var expressionValue: String) {
    var hasDivisionByZero = false
    private val mathSymbolList = listOf('+', '-', '*', '/', '%', '.', '^')

    fun updateExpression(symbol: String) {
        when (symbol) {
            "AC" -> {
                expressionValue = ""
                clearError()
            }
            "CLEAR" -> {
                clearLastSymbol()
                checkDivisionByZero()
            }
            "0" -> {
                expressionValue += symbol
                checkDivisionByZero()
            }
            "%" -> if (!isLastNumberZero()) calculatePercentage()
            "+", "-", "*", "/" -> checkLastSymbol(symbol)
            else -> {
                // Add a character if the last number in the expression is not zero, or there is a dot after zero
                if (!isLastNumberZero() || symbol == ".") expressionValue += symbol

                if (symbol == "=" || symbol == ".") clearError()
            }
        }
    }

    /**
     * Return true if last char in expression is number
     */
    fun isLastCharIsNumber(): Boolean {
        if (expressionValue.isNotBlank()) {
            mathSymbolList.forEach {
                if (expressionValue[expressionValue.length - 1] == it) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * The method checks for duplication of mathematical signs at the end of the expression,
     * and if duplication is found, it overwrites with the last entered sign.
     */
    private fun checkLastSymbol(mathSymbol: String) {
        if (!isLastCharIsNumber()) {
            clearLastSymbol()
        }
        expressionValue += mathSymbol
    }

    private fun calculatePercentage() {
        val lastNumber = getLastNumber()

        //Remove the last number from the expression
        expressionValue = expressionValue.substring(0, expressionValue.lastIndexOf(lastNumber))

        when (expressionValue[expressionValue.lastIndex]) {
            '/', '*' -> {
                expressionValue += lastNumber.toDouble() / 100
            }
            '+', '-' -> {
                val percentValue = resultOfExpression(
                    expressionValue.substring(
                        0,
                        expressionValue.lastIndex
                    )
                ) * (lastNumber.toDouble() / 100)

                expressionValue += if (percentValue.isInteger()) {
                    percentValue.toInt()
                } else percentValue
            }
        }
    }

    private fun checkDivisionByZero() {
        if (expressionValue.length > 1 && isLastNumberZero()) {
            val charBeforeZero = expressionValue[expressionValue.lastIndex - 1]
            hasDivisionByZero = charBeforeZero == '/'
        }
    }

    private fun clearError() {
        hasDivisionByZero = false
    }

    /**
     * Clear last symbol at the expression
     */
    private fun clearLastSymbol() {
        if (expressionValue.isNotBlank()) {
            expressionValue = expressionValue.substring(0, expressionValue.length - 1)
        }
    }

    private fun getLastNumber(): String {
        val numbersList: MutableList<String> = expressionValue.split("+", "*", "/", "-").toMutableList()
        return numbersList[numbersList.size - 1]
    }

    private fun isLastNumberZero(): Boolean {
        return getLastNumber() == "0"
    }

    fun resultOfExpression(expression: String): Double {
        val engine = ScriptEngineManager().getEngineByName("rhino")

        return try {
            engine.eval(expression) as Double
        } catch (e: ScriptException) {
            0.0
        }
    }
}
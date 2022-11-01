package com.gmail.serhiiromanchuk.mycalculator

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import javax.script.ScriptEngineManager
import javax.script.ScriptException
import kotlin.math.floor
import kotlin.math.pow

/**
 * Return true if double is integer
 */
fun Double.isInteger() = (this == floor(this)) && !this.isInfinite()

@Parcelize
data class Expression(
    var expressionValue: String,
    var isDivisionByZero: Boolean,
    var resultOfExpression: Double
) : Parcelable {
    private var isResultSaved = false

    fun updateExpression(symbol: String) {
        when (symbol) {
            "AC" -> {
                clearAllValue()
            }
            "CLEAR" -> {
                expressionValue = clearLastSymbol(expressionValue)
                isDivisionByZero = checkDivisionByZero()
                if (!isLastCharIsNumber()) {
                    resultOfExpression = resultOfExpression(clearLastSymbol(expressionValue))
                }
            }
            "%" -> if (!isLastNumberZero()) calculatePercentage()
            "+", "-", "*", "/", "^" -> {
                if (expressionValue.isBlank()) expressionValue += "0"
                checkPreviousResult()
                checkLastSymbol()
                expressionValue += symbol
            }
            "=" -> {
                clearError()
                isResultSaved = true
            }
            else -> {
                if (symbol == ".") {
                    checkLastSymbol()
                    clearError()
                }

                // Add a character if the last number in the expression is not zero, or there is a dot after zero
                if (!isLastNumberZero() || symbol == ".") {
                    expressionValue += symbol
                }

                if (symbol == "0") isDivisionByZero = checkDivisionByZero()
            }
        }



        if (isLastCharIsNumber() && expressionValue.isNotBlank() && !isDivisionByZero) {
            resultOfExpression = resultOfExpression(expressionValue)
        }
    }

    private fun calculatePercentage() {
        val lastNumber = getLastNumber(expressionValue)

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

    private fun resultOfExpression(expression: String): Double {
        var correctedExpression = expression
        if (expression.contains('^')) {
            correctedExpression = getCorrectExpressionWithExponent(expression)
        }

        val engine = ScriptEngineManager().getEngineByName("rhino")

        return try {
            engine.eval(correctedExpression) as Double
        } catch (e: ScriptException) {
            0.0
        }
    }

    private fun getCorrectExpressionWithExponent(expression: String): String {
        var result = expression
        if (expression.indexOf("^") == 0) {
            result = "0$expression"
        }

        val splitExpressionList = result.split("^")
        if (splitExpressionList.size > 1) {
            for (index in 1 until splitExpressionList.size) {
                val baseExponent = getLastNumber(splitExpressionList[index - 1])
                val exponent = getFirstNumber(splitExpressionList[index])
                val resultOfPow = baseExponent.toDouble().pow(exponent.toDouble())
                result = result.replace("$baseExponent^$exponent", resultOfPow.toString())
            }
        }
        return result
    }

    /**
     * The method checks for duplication of mathematical signs at the end of the expression,
     * and if duplication is found, it overwrites with the last entered sign.
     */
    private fun checkLastSymbol() {
        if (!isLastCharIsNumber()) {
            expressionValue = clearLastSymbol(expressionValue)
        }
    }

    /**
     * Return true if last char in expression is number
     */
    private fun isLastCharIsNumber(): Boolean {
        val mathSymbolList = listOf('+', '-', '*', '/', '%', '^', '.')
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
     * Resets expressionValue and writes the previous result to the beginning of the expression
     * if it was saved by pressing the equals button
     */
    private fun checkPreviousResult() {
        if (isResultSaved) {
            expressionValue = ""
            expressionValue += checkResultForInteger(resultOfExpression)
            isResultSaved = false
        }
    }

    private fun checkDivisionByZero(): Boolean {
        var lastTwoChars = ""
        if (expressionValue.isNotBlank() && expressionValue.length > 2) {
            lastTwoChars = expressionValue.substring(expressionValue.length - 2)
        }
        return lastTwoChars == "/0"
    }

    private fun checkResultForInteger(result: Double): String {
        return if (result.isInteger()) {
            "${result.toInt()}"
        } else "$result"
    }

    private fun clearError() {
        isDivisionByZero = false
    }

    private fun clearAllValue() {
        expressionValue = ""
        resultOfExpression = 0.0
        clearError()
    }

    /**
     * Clear last symbol at the expression
     */
    private fun clearLastSymbol(expression: String): String {
        return if (expression.isNotBlank()) {
            expressionValue.substring(0, expressionValue.length - 1)
        } else ""
    }

    private fun isLastNumberZero(): Boolean {
        return getLastNumber(expressionValue) == "0"
    }

    private fun getLastNumber(expression: String): String {
        val numbersList = getNumbersList(expression)
        return numbersList[numbersList.size - 1]
    }

    private fun getFirstNumber(expression: String): String {
        val numbersList = getNumbersList(expression)
        return numbersList[0]
    }

    private fun getNumbersList(expression: String): MutableList<String> {
        return expression.split("+", "*", "/", "-", "^").toMutableList()
    }
}
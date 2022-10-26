package com.gmail.serhiiromanchuk.mycalculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var expression = Expression("")

    private val _expressionLiveData = MutableLiveData<String>()
    val expressionLiveData: LiveData<String>
        get() = _expressionLiveData

    private val _resultLiveData = MutableLiveData("0")
    val resultLiveData: LiveData<String>
        get() = _resultLiveData

    private val _isErrorLiveData = MutableLiveData(false)
    val isErrorLiveData: LiveData<Boolean>
        get() = _isErrorLiveData

    fun addToExpression(symbol: String) {
        with(expression) {
            updateExpression(symbol)
            _expressionLiveData.value = expressionValue
            _isErrorLiveData.value = hasDivisionByZero
            updateResult(resultOfExpression)
        }

        if (symbol == "AC") {
            _resultLiveData.value = "0"
        }
    }

    private fun updateResult(result: Double) {
        if (result.isInteger()) {
            _resultLiveData.value = "= ${result.toInt()}"
        } else _resultLiveData.value = "= $result"
    }
}
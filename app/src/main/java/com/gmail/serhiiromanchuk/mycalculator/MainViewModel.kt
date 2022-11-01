package com.gmail.serhiiromanchuk.mycalculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _expressionLiveData = MutableLiveData<Expression>()
    val expressionLiveData: LiveData<Expression>
        get() = _expressionLiveData

    fun initExpression(expression: Expression) {
        _expressionLiveData.value = expression
    }

    fun addToExpression(symbol: String) {
        _expressionLiveData.value?.updateExpression(symbol)
        val oldExpression = _expressionLiveData.value
        _expressionLiveData.value = oldExpression?.copy()
    }
}
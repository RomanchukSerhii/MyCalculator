package com.gmail.serhiiromanchuk.mycalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.gmail.serhiiromanchuk.mycalculator.databinding.ActivityMainBinding
import javax.script.ScriptEngineManager
import javax.script.ScriptException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var isError = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        with(binding) {
            oneButton.setOnClickListener { updateExpression(oneButton.text.toString()) }
            twoButton.setOnClickListener { updateExpression(twoButton.text.toString()) }
            threeButton.setOnClickListener { updateExpression(threeButton.text.toString()) }
            fourButton.setOnClickListener { updateExpression(fourButton.text.toString()) }
            fiveButton.setOnClickListener { updateExpression(fiveButton.text.toString()) }
            sixButton.setOnClickListener { updateExpression(sixButton.text.toString()) }
            sevenButton.setOnClickListener { updateExpression(sevenButton.text.toString()) }
            eightButton.setOnClickListener { updateExpression(eightButton.text.toString()) }
            nineButton.setOnClickListener { updateExpression(nineButton.text.toString()) }
            zeroButton.setOnClickListener {
                updateExpression(zeroButton.text.toString())
                checkDivisionByZero()
            }
            pointButton.setOnClickListener { updateExpression(".") }
            minusButton.setOnClickListener { updateExpression("-") }
            plusButton.setOnClickListener { updateExpression("+") }
            multiplicationButton.setOnClickListener { updateExpression("*") }
            divisionButton.setOnClickListener { updateExpression("/") }
            percentButton.setOnClickListener { updateExpression(percentButton.text.toString()) }
            allClearButton.setOnClickListener { updateExpression(allClearButton.text.toString()) }
            clearButton.setOnClickListener {
                updateExpression("CLEAR")
                checkDivisionByZero()
            }
            equals.setOnClickListener { showResult() }
        }

        viewModel.expressionLiveData.observe(this) {
            binding.expressionTextView.text = it
        }
        viewModel.resultLiveData.observe(this) {
            binding.resultTextView.text = it
        }
    }

    private fun showResult() {
        val expressionTextSize = 20F
        val resultTextSize = 34F
        with(binding) {
            expressionTextView.setTextColor(resources.getColor(R.color.gray, null))
            expressionTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, expressionTextSize)
            resultTextView.setTextColor(resources.getColor(R.color.black, null))
            resultTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, resultTextSize)
        }
    }

    private fun checkDivisionByZero() {
        if (viewModel.isErrorLiveData.value == true) {
            binding.resultTextView.setText(R.string.error)
        }
    }

    private fun updateExpression(symbol: String) {
        when (symbol) {
            "AC", "CLEAR", ".", "=" -> viewModel.addToExpression(symbol)
            else -> if (viewModel.isErrorLiveData.value == false) {
                viewModel.addToExpression(symbol)
            }
        }

    }
}
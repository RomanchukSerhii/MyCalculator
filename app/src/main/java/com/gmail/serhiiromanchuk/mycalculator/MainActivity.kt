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
    private var isResultDisplayed = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        with(binding) {
            oneButton.setOnClickListener {
                updateUI()
                updateExpression(oneButton.text.toString())
            }
            twoButton.setOnClickListener {
                updateUI()
                updateExpression(twoButton.text.toString())
            }
            threeButton.setOnClickListener {
                updateUI()
                updateExpression(threeButton.text.toString())
            }
            fourButton.setOnClickListener {
                updateUI()
                updateExpression(fourButton.text.toString())
            }
            fiveButton.setOnClickListener {
                updateUI()
                updateExpression(fiveButton.text.toString())
            }
            sixButton.setOnClickListener {
                updateUI()
                updateExpression(sixButton.text.toString())
            }
            sevenButton.setOnClickListener {
                updateUI()
                updateExpression(sevenButton.text.toString())
            }
            eightButton.setOnClickListener {
                updateUI()
                updateExpression(eightButton.text.toString())
            }
            nineButton.setOnClickListener {
                updateUI()
                updateExpression(nineButton.text.toString())
            }
            zeroButton.setOnClickListener {
                updateUI()
                updateExpression(zeroButton.text.toString())
                checkDivisionByZero()
            }
            degreeButton.setOnClickListener {
                updateUI()
                updateExpression("^")
            }
            pointButton.setOnClickListener {
                updateUI()
                updateExpression(".")
            }
            minusButton.setOnClickListener {
                updateUI()
                updateExpression("-")
            }
            plusButton.setOnClickListener {
                updateUI()
                updateExpression("+")
            }
            multiplicationButton.setOnClickListener {
                updateUI()
                updateExpression("*")
            }
            divisionButton.setOnClickListener {
                updateUI()
                updateExpression("/")
            }
            percentButton.setOnClickListener {
                updateUI()
                updateExpression(percentButton.text.toString())
            }
            allClearButton.setOnClickListener {
                updateExpression(allClearButton.text.toString())
                isResultDisplayed = true
                showResult()
            }
            clearButton.setOnClickListener {
                updateUI()
                updateExpression("CLEAR")
                checkDivisionByZero()
            }
            equals.setOnClickListener {
                updateExpression(equals.text.toString())
                isResultDisplayed = true
                showResult()
            }
        }

        viewModel.expressionLiveData.observe(this) {
            val correctedExpression = correctTheExpression(it)
            binding.expressionTextView.text = correctedExpression
        }
        viewModel.resultLiveData.observe(this) {
            binding.resultTextView.text = it
        }
    }

    private fun showResult() {
        if (isResultDisplayed) {
            val expressionTextSize = 20F
            val resultTextSize = 34F
            with(binding) {
                expressionTextView.setTextColor(resources.getColor(R.color.gray, null))
                expressionTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, expressionTextSize)
                resultTextView.setTextColor(resources.getColor(R.color.black, null))
                resultTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, resultTextSize)
            }
        }
    }

    private fun updateUI() {
        if (isResultDisplayed) {
            val expressionTextSize = 30F
            val resultTextSize = 24F
            with(binding) {
                expressionTextView.setTextColor(resources.getColor(R.color.black, null))
                expressionTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, expressionTextSize)
                resultTextView.setTextColor(resources.getColor(R.color.gray, null))
                resultTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, resultTextSize)
            }
            isResultDisplayed = false
        }
    }

    private fun checkDivisionByZero() {
        if (viewModel.isErrorLiveData.value == true) {
            binding.resultTextView.setText(R.string.error)
        }
    }

    private fun correctTheExpression(expression: String): String {
        return expression
            .replace('/', '÷')
            .replace('*', '×')
            .replace('.', ',')

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
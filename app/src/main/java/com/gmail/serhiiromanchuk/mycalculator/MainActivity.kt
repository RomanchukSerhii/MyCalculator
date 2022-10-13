package com.gmail.serhiiromanchuk.mycalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.gmail.serhiiromanchuk.mycalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

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
            zeroButton.setOnClickListener { updateExpression(zeroButton.text.toString()) }
            pointButton.setOnClickListener { updateExpression(pointButton.text.toString()) }
            minusButton.setOnClickListener { updateExpression("-") }
            plusButton.setOnClickListener { updateExpression("+") }
            multiplicationButton.setOnClickListener { updateExpression("*") }
            divisionButton.setOnClickListener { updateExpression("/") }
            percentButton.setOnClickListener { updateExpression(percentButton.text.toString()) }
            allClearButton.setOnClickListener { updateExpression(allClearButton.text.toString()) }
            clearButton.setOnClickListener { updateExpression("CLEAR") }
        }
        viewModel.expressionLiveData.observe(this) {
            binding.expressionTextView.text = it
        }
        viewModel.resultLiveData.observe(this) {
            binding.resultTextView.text = it
        }
    }

    private fun updateExpression(number: String) {
        viewModel.addToExpression(number)
    }
}
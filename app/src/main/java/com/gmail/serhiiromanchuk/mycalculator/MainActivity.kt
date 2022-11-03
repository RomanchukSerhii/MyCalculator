package com.gmail.serhiiromanchuk.mycalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.ViewModelProvider
import com.gmail.serhiiromanchuk.mycalculator.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var toggle: ActionBarDrawerToggle
    private var isResultDisplayed = false
    private var isNewExpression = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.tape_length -> Toast.makeText(this,
                    "Clicked", Toast.LENGTH_SHORT).show()
                R.id.basket_price -> Toast.makeText(this,
                    "Clicked", Toast.LENGTH_SHORT).show()
                R.id.bucket_volume -> Toast.makeText(this,
                    "Clicked", Toast.LENGTH_SHORT).show()
                R.id.circuit -> Toast.makeText(this,
                    "Clicked", Toast.LENGTH_SHORT).show()
            }
            true
        }

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.initExpression(
            savedInstanceState?.getParcelable(KEY_EXPRESSION) ?: Expression(
                expressionValue = "",
                isDivisionByZero = false,
                resultOfExpression = 0.0
            )
        )

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
            degreeButton.setOnClickListener { updateExpression("^") }
            pointButton.setOnClickListener {
                updateExpression(".")
                checkDivisionByZero()
            }
            minusButton.setOnClickListener { updateExpression("-") }
            plusButton.setOnClickListener { updateExpression("+") }
            multiplicationButton.setOnClickListener { updateExpression("*") }
            divisionButton.setOnClickListener { updateExpression("/") }
            percentButton.setOnClickListener { updateExpression(percentButton.text.toString()) }
            allClearButton.setOnClickListener {
                isResultDisplayed = true
                isNewExpression = true
                updateExpression(allClearButton.text.toString())
            }
            clearButton.setOnClickListener {
                updateExpression("CLEAR")
                checkDivisionByZero()
                if (viewModel.expressionLiveData.value?.expressionValue == "") {
                    isResultDisplayed = true
                    isNewExpression = true
                    updateExpression(allClearButton.text.toString())
                }
            }
            equals.setOnClickListener {
                isResultDisplayed = true
                updateExpression(equals.text.toString())
            }
        }

        viewModel.expressionLiveData.observe(this) {
            updateUI(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(KEY_EXPRESSION, viewModel.expressionLiveData.value)
        super.onSaveInstanceState(outState)
    }

    private fun updateUI(expression: Expression) {
        val expressionTextSize: Float
        val expressionTextColor: Int
        var resultTextSize: Float
        var resultTextColor: Int
        val correctedExpression = correctTheExpression(expression.expressionValue)
        val correctedResult = correctTheResult(expression.resultOfExpression)

        if (isResultDisplayed) {
            expressionTextSize = resources.getDimension(R.dimen.expression_h2)
            resultTextSize = resources.getDimension(R.dimen.result_h1)
            expressionTextColor = R.color.gray
            resultTextColor = R.color.black
        } else {
            expressionTextSize = resources.getDimension(R.dimen.expression_h1)
            resultTextSize = resources.getDimension(R.dimen.result_h2)
            expressionTextColor = R.color.black
            resultTextColor = R.color.gray
        }

        with(binding) {
            if (isNewExpression) {
                expressionTextView.text = ""
                resultTextView.text = getString(R.string.zero)
                resultTextSize = resources.getDimension(R.dimen.result_h1)
                resultTextColor = R.color.black
            } else {
                expressionTextView.text = correctedExpression
                resultTextView.text = correctedResult
            }

            expressionTextView.setTextColor(resources.getColor(expressionTextColor, null))
            expressionTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, expressionTextSize)
            resultTextView.setTextColor(resources.getColor(resultTextColor, null))
            resultTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resultTextSize)
        }

        isResultDisplayed = false
        isNewExpression = false
    }

    private fun checkDivisionByZero() {
        var lastThreeChars = ""
        val expression = viewModel.expressionLiveData.value?.expressionValue
        val isDivisionByZero = viewModel.expressionLiveData.value?.isDivisionByZero
        if (expression != null && expression.length > 3) {
            lastThreeChars = expression.substring(expression.length - 3)
        }
        if (isDivisionByZero == true || lastThreeChars == "/0.") {
            binding.resultTextView.setText(R.string.error)
        }
    }

    private fun correctTheExpression(expression: String): String {
        return expression
            .replace('/', '÷')
            .replace('*', '×')
            .replace('.', ',')

    }

    private fun correctTheResult(result: Double): String {
        return if (result.isInteger()) {
            "= ${result.toInt()}"
        } else "= $result"
    }

    private fun updateExpression(symbol: String) {
        when (symbol) {
            "AC", "CLEAR", ".", "=" -> viewModel.addToExpression(symbol)
            else -> if (viewModel.expressionLiveData.value?.isDivisionByZero == false) {
                viewModel.addToExpression(symbol)
            }
        }
    }

    companion object {
        private const val KEY_EXPRESSION = "KEY_EXPRESSION"
    }
}
package com.a2k.calculator

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.a2k.calculator.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import com.notkamui.keval.Keval
import com.notkamui.keval.KevalInvalidExpressionException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.navigationBarDividerColor =
            ContextCompat.getColor(this, android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        /*setSupportActionBar(binding.toolbar) */
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top, bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        val numberUI = arrayListOf<String>(
            "^", "C", "<", "/",
            "7", "8", "9", "×",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "", "0", ".", "="
        )

        numberUI.forEach { i ->
            val numberButton = MaterialButton(this)
            numberButton.id = View.generateViewId()

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.width = 200
            params.height = 200

            numberButton.layoutParams = params
            numberButton.text = i.toString()
            numberButton.setOnClickListener {
                when (i) {
                    "C" -> {
                        binding.expressionTextView.text = ""
                    }

                    "=" -> {
                        try {

                            val calculatedResult =
                                Keval{
                                    includeDefault()
                                    operator {
                                        symbol = '×'
                                        precedence = 3
                                        isLeftAssociative = true
                                        implementation = { a, b -> a * b }
                                    }
                                }.eval(binding.expressionTextView.text.toString())
                            var formattedResult = calculatedResult.toString()
                            if (calculatedResult.equals(calculatedResult.toInt().toDouble())) {
                                formattedResult = calculatedResult.toInt().toString()
                            }
                            binding.expressionTextView.text = formattedResult

                        } catch (e: KevalInvalidExpressionException) {
                            Toast.makeText(baseContext, "Invalid Syntax", Toast.LENGTH_SHORT).show()
                        }
                    }

                    "<" -> {
                        val expression = binding.expressionTextView.text
                        if (expression.isNotEmpty()) {
                            binding.expressionTextView.text =
                                expression.subSequence(0, expression.length - 1)
                        }
                    }

                    else -> binding.expressionTextView.text =
                        binding.expressionTextView.text.toString() + i
                }
            }

            numberButton.setOnLongClickListener {
                when (i) {
                    "<" -> binding.expressionTextView.text = ""
                    else -> Unit
                }
                return@setOnLongClickListener false
            }

            binding.root.addView(numberButton)
            val referenceIds = binding.numbersFlow.referencedIds.toMutableList()
            referenceIds.add(numberButton.id)
            binding.numbersFlow.referencedIds = referenceIds.toIntArray()

        }


    }
}


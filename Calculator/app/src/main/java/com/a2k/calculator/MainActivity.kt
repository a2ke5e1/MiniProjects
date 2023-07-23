package com.a2k.calculator

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.updateLayoutParams
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
        /*setSupportActionBar(binding.toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }*/

        for (i in 9 downTo 0) {
            val numberButton = MaterialButton(this)
            numberButton.id = View.generateViewId()
            numberButton.text = i.toString()
            numberButton.setOnClickListener {
                binding.expressionTextView.text = binding.expressionTextView.text.toString() + i.toString()
            }
            binding.root.addView(numberButton)
            val referenceIds = binding.numbersFlow.referencedIds.toMutableList()
            referenceIds.add(numberButton.id)
            binding.numbersFlow.referencedIds = referenceIds.toIntArray()
        }

        val operations = listOf("+", "-", "*", "/",  "<", ".", "=",)
        operations.forEach {i ->
            val numberButton = MaterialButton(this)
            numberButton.id = View.generateViewId()
            numberButton.text = i.toString()

            numberButton.setOnClickListener {
                when (i) {
                    "=" -> {
                        try {
                        binding.expressionTextView.text =
                            Keval.eval(binding.expressionTextView.text.toString()).toString()
                        } catch (e: KevalInvalidExpressionException) {
                            Toast.makeText(baseContext, "Invalid Syntax", Toast.LENGTH_SHORT).show()
                        }
                    }
                    "<" -> {
                        val expression = binding.expressionTextView.text
                        if (expression.isNotEmpty()) {
                            binding.expressionTextView.text = expression.subSequence(0, expression.length - 1)
                        }
                    }
                    else -> binding.expressionTextView.text = binding.expressionTextView.text.toString() + i
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
            val referenceIds = binding.operationsFlow.referencedIds.toMutableList()
            referenceIds.add(numberButton.id)
            binding.operationsFlow.referencedIds = referenceIds.toIntArray()
        }


    }
}


package com.a2k.currencyconverter

import android.R
import android.icu.util.Currency
import android.icu.util.CurrencyAmount
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.a2k.currencyconverter.databinding.ActivityMainBinding
import com.a2k.currencyconverter.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.navigationBarDividerColor =
            ContextCompat.getColor(this, android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setSupportActionBar(binding.toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }


        binding.from.setText("INR", false)
        binding.to.setText("USD", false)

        binding.from.setOnItemClickListener { adapterView, view, i, l ->
            binding.valueContainer.prefixText =  CurrencyAmount(
                0,
                Currency.getInstance(binding.from.text.toString())
            ).currency.symbol.toString()
        }


        binding.conBtn.setOnClickListener {

            viewModel.convert(
                binding.value.text.toString(),
                binding.from.text.toString(),
                binding.to.text.toString(),
            )
        }

        lifecycleScope.launch {
            viewModel.conversion.collect { event ->
                when (event) {
                    is MainViewModel.CurrencyEvent.Success -> {
                        val currency =  CurrencyAmount(
                            event.resultText.toDouble(),
                            Currency.getInstance(binding.to.text.toString())
                        )
                        binding.progressBar.isVisible = false
                        binding.result.text = "${currency.currency.symbol} ${currency.number}"
                    }

                    is MainViewModel.CurrencyEvent.Failure -> {
                        binding.progressBar.isVisible = false
                        Toast.makeText(baseContext, event.errorText, Toast.LENGTH_LONG).show()
                    }

                    is MainViewModel.CurrencyEvent.Loading -> {
                        binding.progressBar.isVisible = true
                    }

                    else -> Unit
                }


            }
        }


    }


}
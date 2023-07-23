package com.a2k.currencyconverter

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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


        binding.conBtn.setOnClickListener {

            viewModel.convert(
                binding.value.text.toString(),
                binding.from.selectedItem.toString(),
                binding.to.selectedItem.toString(),
            )
        }

        lifecycleScope.launch {
            viewModel.conversion.collect { event ->
                when (event) {
                    is MainViewModel.CurrencyEvent.Success -> {

                        binding.progressBar.isVisible = false
                        binding.result.setText(event.resultText)
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
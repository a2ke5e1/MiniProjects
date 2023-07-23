package com.a2k.currencyconverter.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a2k.currencyconverter.util.DispatcherProvider
import com.a2k.currencyconverter.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    sealed class CurrencyEvent {
        class Success(val resultText: String) : CurrencyEvent()
        class Failure(val errorText: String) : CurrencyEvent()
        object Loading : CurrencyEvent()
        object Empty : CurrencyEvent()
    }

    private val _conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    val conversion: StateFlow<CurrencyEvent> = _conversion

    fun convert(
        amountStr: String,
        fromCurr: String,
        toCurr: String
    ) {


        val fromAmount = amountStr.toFloatOrNull()
        if (fromAmount == null) {
            _conversion.value = CurrencyEvent.Failure("Not a number")
            return
        }

        viewModelScope.launch(dispatcher.io) {
            _conversion.value = CurrencyEvent.Loading

            when (val ratesResponse = repository.getRates()) {
                is Resource.Error -> {
                    _conversion.value = CurrencyEvent.Failure(ratesResponse.message.toString())
                }
                is Resource.Success -> {

                    val rates = ratesResponse.data?.rates

                    val x = rates?.getOrDefault(fromCurr, null)
                    val y = rates?.getOrDefault(toCurr, null)

                    if (x == null || y == null){
                        _conversion.value = CurrencyEvent.Failure("Unexpected Error")
                    } else {
                        val convertedCurr = round(fromAmount * (1 / x )* y * 100) / 100
                        _conversion.value = CurrencyEvent.Success(
                            "$convertedCurr"
                        )
                    }

                }
            }
        }

    }
}
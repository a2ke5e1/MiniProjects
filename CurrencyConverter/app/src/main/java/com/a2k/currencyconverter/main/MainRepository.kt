package com.a2k.currencyconverter.main

import com.a2k.currencyconverter.data.models.CurrencyResponse
import com.a2k.currencyconverter.util.Resource

interface MainRepository {

    suspend fun getRates(): Resource<CurrencyResponse>


}
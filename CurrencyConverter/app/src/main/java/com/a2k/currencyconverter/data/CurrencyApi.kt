package com.a2k.currencyconverter.data

import com.a2k.currencyconverter.data.models.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET

interface CurrencyApi {

    //@GET("latest?access_key=1cefcd38e57417652060f96137a274f6")
    @GET("latest")
    suspend fun getRates(): Response<CurrencyResponse>

}
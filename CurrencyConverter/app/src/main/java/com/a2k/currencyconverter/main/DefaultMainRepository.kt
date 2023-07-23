package com.a2k.currencyconverter.main

import android.util.Log
import com.a2k.currencyconverter.data.CurrencyApi
import com.a2k.currencyconverter.data.models.CurrencyResponse
import com.a2k.currencyconverter.util.Resource
import javax.inject.Inject


class DefaultMainRepository @Inject constructor(
    private val api: CurrencyApi
) : MainRepository {
    override suspend fun getRates(): Resource<CurrencyResponse> {
        return try {
            val res = api.getRates()
            val result = res.body()
            if (res.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(res.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error Occurred")
        }
    }


}
package com.a2k.currencyconverter.di

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.a2k.currencyconverter.R
import com.a2k.currencyconverter.data.CurrencyApi
import com.a2k.currencyconverter.main.DefaultMainRepository
import com.a2k.currencyconverter.main.MainRepository
import com.a2k.currencyconverter.util.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


private const val BASE_URL = "http://api.exchangeratesapi.io/"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAPIKey(@ApplicationContext context: Context): String = context.getString(R.string.api_key)


    @Singleton
    @Provides
    fun provideOkHttpClient(@ApplicationContext context: Context, apiKey: String): OkHttpClient =
        OkHttpClient.Builder()
            .cache(Cache(context.cacheDir, 5242880))
            .addInterceptor { chain ->
                var request = chain.request()
                request = if (hasNetwork(context))
                    request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
                else
                    request.newBuilder().header(
                        "Cache-Control",
                        "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7
                    ).build()

                val url = request.url().newBuilder()
                    .addQueryParameter("access_key", apiKey).build()
                request = request.newBuilder().url(url).build()
                chain.proceed(request)
            }
            .build()


    @Singleton
    @Provides
    fun provideCurrencyApi(okHttpClient: OkHttpClient): CurrencyApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
        .create(CurrencyApi::class.java)


    @Singleton
    @Provides
    fun providesMainRepository(api: CurrencyApi): MainRepository = DefaultMainRepository(api)


    @Singleton
    @Provides
    fun providesDispatchers(): DispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined

    }

    private fun hasNetwork(context: Context): Boolean {
        var isConnected: Boolean = false // Initial Value
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
        return isConnected
    }


}
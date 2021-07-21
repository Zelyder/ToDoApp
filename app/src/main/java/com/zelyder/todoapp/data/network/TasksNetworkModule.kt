package com.zelyder.todoapp.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.zelyder.todoapp.data.YANDEX_API_BASE_URL
import com.zelyder.todoapp.data.YANDEX_API_TOKEN
import com.zelyder.todoapp.data.network.api.YandexApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ExperimentalSerializationApi
class TasksNetworkModule @Inject constructor(): TasksNetworkClient {

    private val jsonFormat = Json {
        ignoreUnknownKeys = true
    }

    private val httpClient = OkHttpClient.Builder().addInterceptor(
        Interceptor {
            val original = it.request()
            val request = original.newBuilder()
                .addHeader("Authorization", "Bearer $YANDEX_API_TOKEN")
                .build()
            it.proceed(request)
        }
    )
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val yandexRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(YANDEX_API_BASE_URL)
        .client(httpClient)
        .addConverterFactory(jsonFormat.asConverterFactory("application/json".toMediaType()))
        .build()


    override fun yandexApi(): YandexApi = yandexRetrofit.create()
}
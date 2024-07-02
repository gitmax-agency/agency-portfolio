package com.hypelist.data.di

import com.google.firebase.auth.FirebaseAuth
import com.hypelist.data.hypelist.api.CreationApi
import com.hypelist.data.hypelist.api.HypelistsApi
import com.hypelist.data.user.api.UserInformationApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

fun getApiModule(baseURL: String) = module {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY

    val client = OkHttpClient.Builder()
        .connectTimeout(600, TimeUnit.SECONDS)
        .readTimeout(600, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .addInterceptor { chain ->
            val loggedUser = FirebaseAuth.getInstance().currentUser
            val requestBuilder = chain.request().newBuilder()
            if (loggedUser != null) {
                requestBuilder.addHeader("uid", loggedUser.uid)
            }
            chain.proceed(requestBuilder.build())
        }
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    single<UserInformationApi> { retrofit.create() }
    single<CreationApi> { retrofit.create() }
    single<HypelistsApi> { retrofit.create() }
}
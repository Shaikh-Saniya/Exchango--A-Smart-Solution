package com.example.exchango.activity.userprofile.ktobject
import com.example.exchango.activity.userprofile.ktinterface.NumVerifyApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://apilayer.net/api/"

    val instance: NumVerifyApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(NumVerifyApi::class.java)
    }
}

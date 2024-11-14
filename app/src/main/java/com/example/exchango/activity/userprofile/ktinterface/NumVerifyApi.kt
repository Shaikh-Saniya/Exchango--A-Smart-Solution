package com.example.exchango.activity.userprofile.ktinterface

import com.example.exchango.activity.userprofile.ktdataclass.PhoneNumberValidationResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NumVerifyApi {
    @GET("validate")
    fun validatePhoneNumber(
        @Query("access_key") apiKey: String,
        @Query("number") phoneNumber: String
    ): Call<PhoneNumberValidationResponse>
}

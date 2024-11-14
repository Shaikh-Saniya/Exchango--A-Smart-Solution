package com.example.exchango.activity.userprofile.ktdataclass

data class PhoneNumberValidationResponse(
    val valid: Boolean,
    val number: String,
    val local_format: String,
    val international_format: String,
    val country_code: String,
    val country_name: String,
    val carrier: String?
)



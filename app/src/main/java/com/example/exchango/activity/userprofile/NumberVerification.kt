package com.example.exchango.activity.userprofile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.exchango.R
import com.example.exchango.activity.userprofile.ktdataclass.PhoneNumberValidationResponse
import com.example.exchango.activity.userprofile.ktobject.RetrofitClient
import com.example.exchango.databinding.ActivityNumberVerificationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NumberVerification : AppCompatActivity() {
    private lateinit var phoneNumber: String
    private lateinit var binding: ActivityNumberVerificationBinding

    private lateinit var otp1EditText: EditText
    private lateinit var otp2EditText: EditText
    private lateinit var otp3EditText: EditText
    private lateinit var otp4EditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        phoneNumber = intent.getStringExtra("Number") ?: ""
        binding = ActivityNumberVerificationBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        otp1EditText = binding.otp1
        otp2EditText = binding.otp2
        otp3EditText = binding.otp3
        otp4EditText = binding.otp4

        if (phoneNumber.isNotEmpty()) {
            Log.d("check", "Validating phone number: $phoneNumber")
            validatePhoneNumber(phoneNumber)
        } else {
            Toast.makeText(this, "Enter a valid phone number.", Toast.LENGTH_LONG).show()
        }

        otp1EditText.requestFocus()
    }

    private fun validatePhoneNumber(phoneNumber: String) {
        Log.d("check", "validatePhoneNumber() called ")
        val apiKey = "c52acc511971991632e65209c597fc01"
        Log.d("check", "api key")
        val call = RetrofitClient.instance.validatePhoneNumber(apiKey, phoneNumber)

        call.enqueue(object : Callback<PhoneNumberValidationResponse> {
            override fun onResponse(call: Call<PhoneNumberValidationResponse>, response: Response<PhoneNumberValidationResponse>) {
                if (response.isSuccessful) {
                    Log.d("check", "response.isSuccessful ")
                    val validationResponse = response.body()
                    Log.d("check", "validationResponse = response.body()")
                    if (validationResponse != null && validationResponse.valid) {
                        Toast.makeText(this@NumberVerification, "Valid number: ${validationResponse.international_format}", Toast.LENGTH_SHORT).show()
                        Log.d("check", "Valid number")
                        val i= Intent(this@NumberVerification,Profile::class.java)
                        startActivity(i)
                    } else {
                        Toast.makeText(this@NumberVerification, "Invalid number", Toast.LENGTH_LONG).show()
                        Log.d("check", "inValid number")
                    }
                } else {
                    Toast.makeText(this@NumberVerification, "Error: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                    Log.d("check","Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<PhoneNumberValidationResponse>, t: Throwable) {
                Log.e("check", t.message.toString())
                Toast.makeText(this@NumberVerification, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}

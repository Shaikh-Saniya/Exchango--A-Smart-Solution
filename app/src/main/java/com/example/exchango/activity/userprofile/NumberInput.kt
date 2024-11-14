package com.example.exchango.activity.userprofile

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.exchango.activity.main.WelcomeActivity
import com.example.exchango.databinding.ActivityNumberInputBinding


class NumberInput : AppCompatActivity() {

    private lateinit var binding: ActivityNumberInputBinding
    private lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNumberInputBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this@NumberInput, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.nextBtn.setOnClickListener {
            phoneNumber = binding.editTextPhoneNumber.text.toString().trim()

            val i=Intent(this@NumberInput,NumberVerification::class.java)
            i.putExtra("Number",formatPhoneNumber(phoneNumber))
            startActivity(i)
        }
    }

    private fun formatPhoneNumber(input: String): String {
        return if (input.startsWith("+")) {
            input
        } else {
            "+91$input"
        }
    }


}

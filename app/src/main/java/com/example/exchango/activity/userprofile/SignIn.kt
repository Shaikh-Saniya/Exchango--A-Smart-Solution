package com.example.exchango.activity.userprofile

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.exchango.R
import com.example.exchango.activity.invite.InviteActivity
import com.example.exchango.databinding.ActivityEmailAuthScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import kotlin.random.Random

class SignIn : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityEmailAuthScreenBinding
    private lateinit var handler: Handler
    private lateinit var db: FirebaseFirestore
    private  var emailVerified: Int=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Ensure the activity theme supports edge-to-edge display
        Log.d("EmailAuthScreen", "Entered the activity")

        binding = ActivityEmailAuthScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.verifyBtn.setOnClickListener {
            val emailText = binding.emailText.text.trim().toString()
            if (TextUtils.isEmpty(emailText)) {
                binding.emailText.error = "Email is required"
            } else {
                checkUserDocument(emailText)
            }
        }

        binding.nextBtn.setOnClickListener{
            if(emailVerified==1){
                startActivity(Intent(this, InviteActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this, Profile::class.java))
                finish()
            }
        }
    }

    private fun signUpUser(email: String) {
        // Generate a random password
        val randomPassword = Random.nextInt(1000000000, 9999999999.toInt())
        auth.createUserWithEmailAndPassword(email, randomPassword.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser!!
                    user.sendEmailVerification().addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Verification email sent to ${user.email}. Please verify your email from the provided link.",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            Toast.makeText(
                                this,
                                "Failed to send verification email: ${verificationTask.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Sign-up failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

//    private fun startVerificationCheckLoop(user: FirebaseUser) {
//        handler = Handler(Looper.getMainLooper())
//        handler.postDelayed(object : Runnable {
//            override fun run() {
//                user.reload().addOnCompleteListener { task ->
//                    if (task.isSuccessful && user.isEmailVerified) {
//                        Toast.makeText(this@EmailAuthScreen, "Email verified!", Toast.LENGTH_SHORT).show()
//                        navigateToNextActivity()
//                        handler.removeCallbacks(this)  // Stop the loop
//                    } else {
//                        handler.postDelayed(this, 5000)  // Check again in 5 seconds
//                    }
//                }
//            }
//        }, 5000)
//    }


    private fun checkUserDocument(email: String) {
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods
                if (signInMethods.isNullOrEmpty()) {
                    signUpUser(email)
                    emailVerified=1
                } else {
                   emailVerified=0
                }
            } else {
                Toast.makeText(this, "Document fetch failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Document fetch failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

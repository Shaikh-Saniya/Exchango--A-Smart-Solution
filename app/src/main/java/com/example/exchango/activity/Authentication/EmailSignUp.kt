package com.example.exchango.activity.Authentication

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.exchango.R
import com.example.exchango.databinding.ActivityEmailSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class EmailSignUp : AppCompatActivity() {

    private lateinit var binding: ActivityEmailSignUpBinding
    private val auth=FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEmailSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.nextBtn.setOnClickListener {
            val email = binding.emailTfTxt.text.toString().trim()
            val password = binding.passwordTfTxt.text.toString().trim()
            val confirmPassword= binding.confirmpassTfTxt.text.toString().trim()
            authentication(email, password, confirmPassword)
        }
    }

    private fun authentication(email: String?, password: String?, confirmPassword: String?) {
        binding.emailTfLy.error = null
        binding.passwordTfLyt.error = null
        binding.confirmPassTfLyt.error = null
        if (!email.isNullOrEmpty() && !password.isNullOrEmpty()&& !confirmPassword.isNullOrEmpty()) {
            if(password==confirmPassword){
                auth.createUserWithEmailAndPassword( email, password).addOnCompleteListener(this) { task->
                    if(task.isSuccessful){
                        val user= auth.currentUser
                        user?.sendEmailVerification()?.addOnCompleteListener(this){ task ->
                            if(task.isSuccessful){
                                motionToast("Check ur email box and click on the link to verify ur details",MotionToastStyle.INFO)
                            }else{
                                val errorMessage= task.exception?.message
                                if (errorMessage != null) {
                                    when{
                                        errorMessage.contains("This email address is badly formated ")==true->{
                                            binding.emailTfLy.error="Email is badly formated"
                                            motionToast("Email address is missing some characters or is incorrectly formatted",MotionToastStyle.ERROR)
                                        }
                                        errorMessage.contains("The email address is already in use")==true->{
                                            binding.emailTfLy.error="This email is in use "
                                            motionToast("This Email Address is already in use Login or try with another email address",MotionToastStyle.ERROR)
                                        }
                                        errorMessage.contains("A network error (such as timeout,interupted connection or unreachable)")==true->{
                                            motionToast("No Internet (Check ur internet connection)",MotionToastStyle.ERROR)
                                        }
                                        else->{
                                            Log.d("auth","Email Verification Failed try again $errorMessage")
                                            motionToast("Email Verification Failed try again $errorMessage",MotionToastStyle.ERROR)
                                        }
                                    }
                                }

                            }
                        }
                    }else{
                        val errorMessage= task.exception?.message
                        if (errorMessage != null) {
                            when{
                                errorMessage.contains("This account has been disabled")==true-> {
                                    binding.emailTfLy.error = "This account is disable"
                                    motionToast("Kindly contact support", MotionToastStyle.ERROR)
                                }
                                else->{
                                    Log.d("auth","user not created $errorMessage")
                                    motionToast("Email Verification Failed try again $errorMessage",MotionToastStyle.ERROR)
                                }
                            }
                        }

                    }
                }
            }else{
                binding.confirmPassTfLyt.error="Invalid Confrim Password"
                motionToast("Password and Confirm Password does not matches", MotionToastStyle.ERROR)
            }
        } else {
            if (email.isNullOrEmpty()) {
                binding.confirmPassTfLyt.error="Invalid Confrim Password"
                motionToast("Enter the email in the box", MotionToastStyle.ERROR)
            }else if (password.isNullOrEmpty()) {
                binding.confirmPassTfLyt.error="Password cannot be empty"
                motionToast("Enter the password in the box", MotionToastStyle.ERROR)
            } else if (confirmPassword.isNullOrEmpty()) {
                binding.confirmPassTfLyt.error="Confirm Password cannotbe empty"
                motionToast("Enter the password in the confirm password box", MotionToastStyle.ERROR)
            }else{
                motionToast("Enter the credentials in the boxes", MotionToastStyle.ERROR)
            }
        }
    }
    private fun motionToast(msg: String, msgType: MotionToastStyle) {
        MotionToast.createToast(
            this,
            msg,
            "Unknown error",
            msgType,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this, R.font.zen_antique)
        )
    }
}

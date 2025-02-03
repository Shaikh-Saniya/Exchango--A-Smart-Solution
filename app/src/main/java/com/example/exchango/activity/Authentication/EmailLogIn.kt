package com.example.exchango.activity.Authentication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.exchango.R
import com.example.exchango.activity.invite.InviteActivity
import com.example.exchango.activity.userprofile.Profile
import com.example.exchango.databinding.ActivityEmailLogInBinding
import com.google.android.material.R.attr.colorOnPrimary
import com.google.android.material.R.attr.colorSecondary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import kotlin.random.Random

class EmailLogIn : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityEmailLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityEmailLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth=FirebaseAuth.getInstance()

        binding.backButton.setOnClickListener {
            finish()
        }
        
        binding.nextBtn.setOnClickListener {
            val email= binding.emailTfTxt.text.toString().trim()
            Log.d("auth",email)
            val password= binding.passwordTfTxt.text.toString().trim()
            Log.d("auth",password)
            userLogin(email, password)
        }
    }

    private fun userLogin(email: String, password: String) {
        binding.emailTfLyt.error=null
        binding.passwordTfLyt.error=null
        if(! email.isNullOrEmpty() && ! password.isNullOrEmpty()){
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task->
                if(task .isSuccessful){
                    val user=auth.currentUser
                    if (user != null) {
                        if(user.isEmailVerified){
                            motionToast("Credentials Verified",MotionToastStyle.SUCCESS)
                            startActivity(Intent(this, Profile::class.java))
                            finish()
                        }else{
                            motionToast("User not found",MotionToastStyle.ERROR)
                        }
                    }
                }else{
                    val errMsg=task.exception?.message
                    motionToast("$errMsg",MotionToastStyle.ERROR)
                }
            }
        }else{
            if(email.isEmpty()&& password.isNotEmpty()){
                binding.emailTfLyt.error="Email cannot be empty"
                motionToast("Enter Email in the box",MotionToastStyle.ERROR)
            }else if(email.isNotEmpty() &&password.isEmpty()){
                binding.passwordTfLyt.error="Password cannot be empty"
                motionToast("Enter Password in the box",MotionToastStyle.ERROR)
            }else{
                motionToast("Enter the credentials in the boxes",MotionToastStyle.ERROR)
            }
        }
    }

    private fun motionToast (msg:String,msgType:MotionToastStyle){
        MotionToast.createToast(
            this,
            "Authentication Failed",
            msg,
            msgType,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this,R.font.zen_antique)
        )
    }


}

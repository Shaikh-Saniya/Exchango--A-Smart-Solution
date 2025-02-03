package com.example.exchango.activity.userprofile

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.exchango.R
import com.example.exchango.activity.invite.InviteActivity
import com.example.exchango.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.UUID
import android.util.Base64

class Profile : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var db: FirebaseFirestore// Circular image for profile
    private lateinit var storageReference: StorageReference
    private lateinit var storage: FirebaseStorage
    private val REQUEST_CODE_PICK_IMAGE = 1
    private val REQUEST_CODE_PERMISSION = 2
    private var base64Image=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflate layout and set content
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase and views
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ParentLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Save button logic
        binding.saveButton.setOnClickListener {
            val name = binding.nameTextfieldTxt.text.toString()
            val about = binding.aboutTextfieldText.text.toString()
            val number = binding.numberTextfieldTxt.text.toString()
            saveUserData(name, about, number, base64Image)
            startActivity(Intent(this@Profile, InviteActivity::class.java))
        }

        // Back button logic
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        // Profile image set button logic
        binding.setProfileImage.setOnClickListener {
            val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // Request permission
                ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_CODE_PERMISSION)
            } else {
                openImagePicker()  // Permission granted, open image picker
            }
        }
    }

    // Request permission result handler
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open image picker
                openImagePicker()
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission denied to read your external storage", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Open image picker
    private fun openImagePicker() {
        val pickImageIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickImageIntent.type = "image/*"
        startActivityForResult(pickImageIntent, REQUEST_CODE_PICK_IMAGE)
    }

    // Handle image selection result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PICK_IMAGE) {
            val imageUri = data?.data
            imageUri?.let {
                try {
                    val selectedImageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                    base64Image = encodeImageToBase64(selectedImageBitmap)
                    loadProfileImage(base64Image)

                } catch (e: IOException) {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
    

    private fun loadProfileImage(base64Image: String) {
        try {
            // Decode Base64 String to Bitmap
            val decodedByteArray: ByteArray = Base64.decode(base64Image, Base64.DEFAULT)
            val decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)

            // Set the decoded Bitmap to CircleImageView
            binding.profileImage.setImageBitmap(decodedBitmap)

        } catch (e: Exception) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    // Save user data to Firestore
    private fun saveUserData(name: String, about: String, number: String,profileImage:String) {
        val document = db.collection("User Profile").document(user.email!!)
        document.set(hashMapOf(
            "Name" to name,
            "About" to about,
            "Number" to number,
            "Profile Image" to profileImage

        )).addOnSuccessListener {
            Toast.makeText(this@Profile, "Data saved", Toast.LENGTH_LONG).show()
        }.addOnFailureListener { e ->
            Toast.makeText(this@Profile, "${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

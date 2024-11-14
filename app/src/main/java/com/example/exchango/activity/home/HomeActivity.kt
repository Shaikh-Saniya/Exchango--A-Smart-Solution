package com.example.exchango.activity.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exchango.R
import com.example.exchango.activity.chatscreen.ChatScreen
import com.example.exchango.activity.home.adapter.Profile
import com.example.exchango.activity.home.adapter.UserProfileAdapter
import com.example.exchango.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : ComponentActivity() , UserProfileAdapter.OnItemClickListener{


    private lateinit var userNameTxt: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var userProfileAdapter: UserProfileAdapter
    private lateinit var binding: ActivityHomeBinding
    private lateinit var userList: List<Profile>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("HomeActivity", "Home activity started")

        recyclerView = binding.recyclerViewHomepg
        recyclerView.layoutManager = LinearLayoutManager(this)

        userList = listOf(
            Profile("John Doe"),
            Profile("Neha"),
            Profile("Ayesha"),
            Profile("Mehek"),
            Profile("Zainab"),
            Profile("Alviya"),
            Profile("Zara")
        )
        userProfileAdapter = UserProfileAdapter(userList)
        recyclerView.adapter = userProfileAdapter

        userNameTxt = binding.tvUserName
        searchView = binding.searchView

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetchUserName()
        setupSearchView()

        Log.d("HomeActivity", "Home activity initialized successfully")
    }

    private fun fetchUserName() {
        val db = Firebase.firestore
        val userId = FirebaseAuth.getInstance().currentUser?.email ?: return

        db.collection("User Details").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val userName = document.data?.get("name")?.toString() ?: "User"
                Log.d("HomeActivity", "Retrieved username: $userName")
                userNameTxt.text = userName
            }
            .addOnFailureListener { e ->
                Log.e("HomeActivity", "Error fetching user name", e)
                Toast.makeText(this, "Error fetching user name", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupSearchView() {
        searchView.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
            ?.setHintTextColor(ContextCompat.getColor(this, R.color.dark_gray))

        val searchIconView: ImageView = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon)
        searchIconView.drawable?.let {
            it.setTint(ContextCompat.getColor(this, R.color.dark_gray))
            searchIconView.setImageDrawable(it)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterUserList(newText)
                return true
            }
        })
    }

    private fun filterUserList(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            userList
        } else {
            userList.filter { user -> user.name.contains(query, ignoreCase = true) }
        }
        userProfileAdapter.updateList(filteredList)
    }

    override fun onStart() {
        super.onStart()
        Log.d("HomeActivity", "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("HomeActivity", "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d("HomeActivity", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("HomeActivity", "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("HomeActivity", "onDestroy called")
    }
    override fun onItemClick(clickedItem: Profile) {
        // Access the Product directly from clickedItem
        val selectedProduct = clickedItem.name

        if (selectedProduct != null) {
            // Create the intent for ProductDescription activity
            val intent = Intent(this,ChatScreen::class.java)
            intent.putExtra("product", selectedProduct) // Pass the Product object
            Log.d("ProductDescription", "Product passed: ${selectedProduct}")
            startActivity(intent)
        } else {
            Log.d("ProductDescription", "Selected product not found")
        }
    }
}

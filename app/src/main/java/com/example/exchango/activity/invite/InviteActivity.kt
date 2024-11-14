package com.example.exchango.activity.invite

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.exchango.R
import com.example.exchango.activity.home.HomeActivity
import com.example.exchango.activity.invite.adapter.Invite
import com.example.exchango.activity.invite.adapter.InviteList
import com.example.exchango.databinding.ActivityInviteBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exchango.activity.invite.adapter.ContactsPagingSource

class InviteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInviteBinding
    private lateinit var shareLink: String

    private lateinit var pager: Pager<Int, Invite>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityInviteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkContactsPermission()

        binding.btnSkip.setOnClickListener {
            startActivity(Intent(this@InviteActivity, HomeActivity::class.java))
        }

        binding.shareALinkLayout.setOnClickListener {
            shareLink = "https://drive.google.com/file/d/1_VxAeelqJr35MkK2MftJFnoN9GW5gA-6/view?usp=drive_link"
            binding.shareLinkLayout.visibility = View.VISIBLE
            binding.shareLink.text = shareLink
            binding.copyToClipboard.setOnClickListener {
                copyClipToBoard(shareLink)
            }
        }
    }

    private fun checkContactsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 100)
        } else {
            loadContacts()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {
                Toast.makeText(this, "Permission denied to read contacts", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadContacts() {
        pager = Pager(PagingConfig(pageSize = 20)) { ContactsPagingSource(contentResolver) }

        lifecycleScope.launch {
            pager.flow.collectLatest { pagingData ->
                val adapter = InviteList()
                binding.recyclerView.layoutManager = LinearLayoutManager(this@InviteActivity)
                binding.recyclerView.adapter = adapter
                adapter.submitData(pagingData)
            }
        }
    }

    private fun copyClipToBoard(shareLink: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Link", shareLink)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this@InviteActivity, "Link copied to clipboard", Toast.LENGTH_LONG).show()
    }
}

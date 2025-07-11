package com.raffles.siasatuksw

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.raffles.siasatuksw.dashboard.DosenActivity
import com.raffles.siasatuksw.dashboard.KaprogdiActivity
import com.raffles.siasatuksw.dashboard.MahasiswaActivity
import com.raffles.siasatuksw.databinding.ActivityMainBinding
import com.raffles.siasatuksw.model.User
import com.raffles.siasatuksw.utilities.FirebaseManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val firebaseManager = FirebaseManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val userId = binding.etUserId.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (userId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Mohon isi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            performLogin(userId, password)
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin(userId: String, password: String) {
        binding.btnLogin.isEnabled = false
        binding.progressBar.visibility = android.view.View.VISIBLE

        lifecycleScope.launch {
            try {
                val user = firebaseManager.loginUser(userId, password)
                if (user != null) {
                    navigateToUserDashboard(user)
                } else {
                    Toast.makeText(this@MainActivity, "Login gagal. Periksa ID dan password", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnLogin.isEnabled = true
                binding.progressBar.visibility = android.view.View.GONE
            }
        }
    }

    private fun navigateToUserDashboard(user: User) {
        val intent = when (user.userType) {
            "KAPROGDI" -> Intent(this, KaprogdiActivity::class.java)
            "DOSEN" -> Intent(this, DosenActivity::class.java)
            "MAHASISWA" -> Intent(this, MahasiswaActivity::class.java)
            else -> return
        }
        intent.putExtra("USER_ID", user.id)
        intent.putExtra("USER_NAME", user.nama)
        startActivity(intent)
        finish()
    }
}
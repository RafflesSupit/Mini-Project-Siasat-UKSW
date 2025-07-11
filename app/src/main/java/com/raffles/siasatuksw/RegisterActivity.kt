package com.raffles.siasatuksw

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.raffles.siasatuksw.databinding.ActivityRegisterBinding
import com.raffles.siasatuksw.model.User
import com.raffles.siasatuksw.utilities.FirebaseManager
import com.raffles.siasatuksw.utilities.UserValidator
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val firebaseManager = FirebaseManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupSpinner()
        setupClickListeners()
    }

    private fun setupSpinner() {
        val userTypes = arrayOf("KAPROGDI", "DOSEN", "MAHASISWA")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, userTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUserType.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            validateAndRegister()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun validateAndRegister() {
        val userId = binding.etUserId.text.toString().trim()
        val nama = binding.etNama.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val userType = binding.spinnerUserType.selectedItem.toString()

        if (userId.isEmpty() || nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Mohon isi semua field", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate user ID format
        val isValidId = when (userType) {
            "DOSEN" -> UserValidator.validateKodeDosen(userId)
            "MAHASISWA" -> UserValidator.validateKodeMahasiswa(userId)
            else -> true
        }

        if (!isValidId) {
            val message = when (userType) {
                "DOSEN" -> "Kode dosen harus 5 digit dan diawali '67'"
                "MAHASISWA" -> "Kode mahasiswa harus 9-10 digit"
                else -> "Format ID tidak valid"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            return
        }

        val user = User(userId, nama, email, userType, password)
        performRegister(user)
    }

    private fun performRegister(user: User) {
        binding.btnRegister.isEnabled = false
        binding.progressBar.visibility = android.view.View.VISIBLE

        lifecycleScope.launch {
            try {
                val success = firebaseManager.createUser(user)
                if (success) {
                    Toast.makeText(this@RegisterActivity, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Registrasi gagal. Coba lagi.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnRegister.isEnabled = true
                binding.progressBar.visibility = android.view.View.GONE
            }
        }
    }
}
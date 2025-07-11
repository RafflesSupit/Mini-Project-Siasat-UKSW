package com.raffles.siasatuksw.dashboard

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.raffles.siasatuksw.MataKuliahAdapter
import com.raffles.siasatuksw.databinding.ActivityKaprogdiBinding
import com.raffles.siasatuksw.databinding.DialogAddMataKuliahBinding
import com.raffles.siasatuksw.model.MataKuliah
import com.raffles.siasatuksw.utilities.FirebaseManager
import com.raffles.siasatuksw.utilities.UserValidator
import kotlinx.coroutines.launch

class KaprogdiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKaprogdiBinding
    private val firebaseManager = FirebaseManager()
    private lateinit var mataKuliahAdapter: MataKuliahAdapter
    private val mataKuliahList = mutableListOf<MataKuliah>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKaprogdiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val userName = intent.getStringExtra("USER_NAME") ?: ""
        binding.tvWelcome.text = "Selamat datang, $userName"

        setupRecyclerView()
        setupClickListeners()
        loadMataKuliah()
    }

    private fun setupRecyclerView() {
        mataKuliahAdapter = MataKuliahAdapter(mataKuliahList)
        binding.rvMataKuliah.apply {
            layoutManager = LinearLayoutManager(this@KaprogdiActivity)
            adapter = mataKuliahAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnTambahMataKuliah.setOnClickListener {
            showAddMataKuliahDialog()
        }

        binding.btnRefresh.setOnClickListener {
            loadMataKuliah()
        }

        binding.btnLogout.setOnClickListener {
            finish()
        }
    }

    private fun showAddMataKuliahDialog() {
        val dialogBinding = DialogAddMataKuliahBinding.inflate(layoutInflater)

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Tambah Mata Kuliah")
            .setView(dialogBinding.root)
            .setPositiveButton("Tambah") { _, _ ->
                val kode = dialogBinding.etKodeMataKuliah.text.toString().trim()
                val nama = dialogBinding.etNamaMataKuliah.text.toString().trim()
                val sks = dialogBinding.etSks.text.toString().toIntOrNull() ?: 0
                val semester = dialogBinding.etSemester.text.toString().toIntOrNull() ?: 0
                val dosenId = dialogBinding.etDosenId.text.toString().trim()

                if (kode.isEmpty() || nama.isEmpty() || sks <= 0 || semester <= 0 || dosenId.isEmpty()) {
                    Toast.makeText(this, "Mohon isi semua field dengan benar", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (!UserValidator.validateKodeDosen(dosenId)) {
                    Toast.makeText(this, "Kode dosen harus 5 digit dan diawali '67'", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val mataKuliah = MataKuliah(
                    id = "${kode}_${System.currentTimeMillis()}",
                    kode = kode,
                    nama = nama,
                    sks = sks,
                    semester = semester,
                    dosenId = dosenId,
                    createdBy = intent.getStringExtra("USER_ID") ?: ""
                )

                addMataKuliah(mataKuliah)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun addMataKuliah(mataKuliah: MataKuliah) {
        lifecycleScope.launch {
            try {
                val success = firebaseManager.createMataKuliah(mataKuliah)
                if (success) {
                    Toast.makeText(this@KaprogdiActivity, "Mata kuliah berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    loadMataKuliah()
                } else {
                    Toast.makeText(this@KaprogdiActivity, "Gagal menambahkan mata kuliah", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@KaprogdiActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadMataKuliah() {
        lifecycleScope.launch {
            try {
                val mataKuliahData = firebaseManager.getAllMataKuliah()
                mataKuliahList.clear()
                mataKuliahList.addAll(mataKuliahData)
                mataKuliahAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(this@KaprogdiActivity, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
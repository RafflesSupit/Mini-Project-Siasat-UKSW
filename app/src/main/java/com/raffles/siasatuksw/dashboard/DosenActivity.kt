package com.raffles.siasatuksw.dashboard

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.raffles.siasatuksw.MataKuliahDosenAdapter
import com.raffles.siasatuksw.databinding.ActivityDosenBinding
import com.raffles.siasatuksw.databinding.DialogInputNilaiBinding
import com.raffles.siasatuksw.model.MataKuliah
import com.raffles.siasatuksw.model.Nilai
import com.raffles.siasatuksw.utilities.FirebaseManager
import com.raffles.siasatuksw.utilities.UserValidator
import kotlinx.coroutines.launch

class DosenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDosenBinding
    private val firebaseManager = FirebaseManager()
    private lateinit var mataKuliahAdapter: MataKuliahDosenAdapter
    private val mataKuliahList = mutableListOf<MataKuliah>()
    private lateinit var dosenId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDosenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        dosenId = intent.getStringExtra("USER_ID") ?: ""
        val userName = intent.getStringExtra("USER_NAME") ?: ""
        binding.tvWelcome.text = "Selamat datang, $userName"

        setupRecyclerView()
        setupClickListeners()
        loadMataKuliah()
    }

    private fun setupRecyclerView() {
        mataKuliahAdapter = MataKuliahDosenAdapter(mataKuliahList) { mataKuliah ->
            showInputNilaiDialog(mataKuliah)
        }
        binding.rvMataKuliah.apply {
            layoutManager = LinearLayoutManager(this@DosenActivity)
            adapter = mataKuliahAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnRefresh.setOnClickListener {
            loadMataKuliah()
        }

        binding.btnLogout.setOnClickListener {
            finish()
        }
    }

    private fun showInputNilaiDialog(mataKuliah: MataKuliah) {
        val dialogBinding = DialogInputNilaiBinding.inflate(layoutInflater)

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Input Nilai - ${mataKuliah.nama}")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val mahasiswaId = dialogBinding.etMahasiswaId.text.toString().trim()
                val nilaiAngka = dialogBinding.etNilai.text.toString().toDoubleOrNull() ?: 0.0
                val semester = dialogBinding.etSemester.text.toString().toIntOrNull() ?: 0
                val tahunAkademik = dialogBinding.etTahunAkademik.text.toString().trim()

                if (mahasiswaId.isEmpty() || nilaiAngka < 0 || nilaiAngka > 100 || semester <= 0 || tahunAkademik.isEmpty()) {
                    Toast.makeText(this, "Mohon isi semua field dengan benar", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (!UserValidator.validateKodeMahasiswa(mahasiswaId)) {
                    Toast.makeText(this, "Kode mahasiswa harus 9-10 digit", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val grade = UserValidator.hitungGrade(nilaiAngka)
                val nilai = Nilai(
                    id = "${mahasiswaId}_${mataKuliah.id}_${System.currentTimeMillis()}",
                    mahasiswaId = mahasiswaId,
                    mataKuliahId = mataKuliah.id,
                    nilai = nilaiAngka,
                    grade = grade,
                    semester = semester,
                    tahunAkademik = tahunAkademik,
                    inputBy = dosenId
                )

                inputNilai(nilai)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun inputNilai(nilai: Nilai) {
        lifecycleScope.launch {
            try {
                val success = firebaseManager.inputNilai(nilai)
                if (success) {
                    Toast.makeText(this@DosenActivity, "Nilai berhasil diinput", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@DosenActivity, "Gagal menginput nilai", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DosenActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadMataKuliah() {
        lifecycleScope.launch {
            try {
                val mataKuliahData = firebaseManager.getMataKuliahByDosen(dosenId)
                Log.d("","mataKuliah Data: $mataKuliahData")
                mataKuliahList.clear()
                mataKuliahList.addAll(mataKuliahData)
                mataKuliahAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(this@DosenActivity, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
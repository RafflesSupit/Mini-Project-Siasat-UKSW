package com.raffles.siasatuksw.dashboard

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.raffles.siasatuksw.TranskripAdapter
import com.raffles.siasatuksw.databinding.ActivityMahasiswaBinding
import com.raffles.siasatuksw.model.Transkrip
import com.raffles.siasatuksw.utilities.FirebaseManager
import com.raffles.siasatuksw.utilities.UserValidator
import kotlinx.coroutines.launch

class MahasiswaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMahasiswaBinding
    private val firebaseManager = FirebaseManager()
    private lateinit var transkripAdapter: TranskripAdapter
    private val transkripList = mutableListOf<Transkrip>()
    private lateinit var mahasiswaId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMahasiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        mahasiswaId = intent.getStringExtra("USER_ID") ?: ""
        val userName = intent.getStringExtra("USER_NAME") ?: ""
        binding.tvWelcome.text = "Selamat datang, $userName"

        setupRecyclerView()
        setupClickListeners()
        loadTranskrip()
    }

    private fun setupRecyclerView() {
        transkripAdapter = TranskripAdapter(transkripList)
        binding.rvTranskrip.apply {
            layoutManager = LinearLayoutManager(this@MahasiswaActivity)
            adapter = transkripAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnRefresh.setOnClickListener {
            loadTranskrip()
        }

        binding.btnLogout.setOnClickListener {
            finish()
        }
    }

    private fun loadTranskrip() {
        lifecycleScope.launch {
            try {
                val nilaiList = firebaseManager.getNilaiMahasiswa(mahasiswaId)
                val mataKuliahList = firebaseManager.getAllMataKuliah()

                transkripList.clear()

                for (nilai in nilaiList) {
                    val mataKuliah = mataKuliahList.find { it.id == nilai.mataKuliahId }
                    if (mataKuliah != null) {
                        transkripList.add(Transkrip(mataKuliah, nilai))
                    }
                }

                transkripAdapter.notifyDataSetChanged()
                updateStatistik()

            } catch (e: Exception) {
                Toast.makeText(this@MahasiswaActivity, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStatistik() {
        if (transkripList.isEmpty()) {
            binding.tvTotalSks.text = "Total SKS: 0"
            binding.tvIpk.text = "IPK: 0.00"
            return
        }

        var totalSks = 0
        var totalBobot = 0.0

        for (item in transkripList) {
            totalSks += item.mataKuliah.sks
            totalBobot += UserValidator.hitungBobotNilai(item.nilai.grade) * item.mataKuliah.sks
        }

        val ipk = if (totalSks > 0) totalBobot / totalSks else 0.0

        binding.tvTotalSks.text = "Total SKS: $totalSks"
        binding.tvIpk.text = "IPK: ${"%.2f".format(ipk)}"
    }
}
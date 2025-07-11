package com.raffles.siasatuksw

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.raffles.siasatuksw.databinding.ActivityInputNilaiBinding
import com.google.firebase.database.*
import com.raffles.siasatuksw.model.MataKuliah
import com.raffles.siasatuksw.model.Nilai
import com.raffles.siasatuksw.model.TranskipItem
import com.raffles.siasatuksw.model.Transkrip
import com.raffles.siasatuksw.utilities.SessionManager
import com.raffles.siasatuksw.utilities.UserValidator

class InputNilaiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputNilaiBinding
    private lateinit var database: DatabaseReference
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputNilaiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        database = FirebaseDatabase.getInstance("https://siasatuksw-c31ce-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
        sessionManager = SessionManager(this)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnInputNilai.setOnClickListener {
            val mahasiswaId = binding.etMahasiswaId.text.toString().trim()
            val mataKuliahCode = binding.etMataKuliahId.text.toString().trim()  // Changed from mataKuliahId to mataKuliahCode
            val nilai = binding.etNilai.text.toString().trim()
            val semester = binding.etSemester.text.toString().trim()
            val tahun = binding.etTahun.text.toString().trim()

            if (validateInput(mahasiswaId, mataKuliahCode, nilai, semester, tahun)) {
                inputNilai(mahasiswaId, mataKuliahCode, nilai.toDouble(), semester.toInt(), tahun)
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun validateInput(mahasiswaId: String, mataKuliahCode: String, nilai: String, semester: String, tahun: String): Boolean {
        if (mahasiswaId.isEmpty() || mataKuliahCode.isEmpty() || nilai.isEmpty() || semester.isEmpty() || tahun.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi semua field", Toast.LENGTH_SHORT).show()
            return false
        }



        try {
            val nilaiDouble = nilai.toDouble()
            if (nilaiDouble < 0 || nilaiDouble > 100) {
                Toast.makeText(this, "Nilai harus antara 0-100", Toast.LENGTH_SHORT).show()
                return false
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Format nilai tidak valid", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun inputNilai(mahasiswaId: String, mataKuliahCode: String, nilai: Double, semester: Int, tahun: String) {
        val user = sessionManager.getUser() ?: return
        val nilaiId = database.child("nilai").push().key ?: return

        val huruf = UserValidator.hitungGrade(nilai)
        val nilaiObj = Nilai(nilaiId, mahasiswaId, mataKuliahCode, nilai, huruf, semester, tahun, user.id)  // Changed mataKuliahId to mataKuliahCode

        database.child("nilai").child(nilaiId).setValue(nilaiObj)
            .addOnSuccessListener {
                Toast.makeText(this, "Nilai berhasil diinput", Toast.LENGTH_SHORT).show()
                updateTranskrip(mahasiswaId)
                clearForm()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal input nilai: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateTranskrip(mahasiswaId: String) {
        database.child("nilai").orderByChild("mahasiswaId").equalTo(mahasiswaId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nilaiList = mutableListOf<Nilai>()
                    var totalSks = 0
                    var totalNilai = 0.0
                    var processedCount = 0
                    val totalNilaiCount = snapshot.childrenCount.toInt()

                    for (childSnapshot in snapshot.children) {
                        val nilai = childSnapshot.getValue(Nilai::class.java)
                        if (nilai != null) {
                            nilaiList.add(nilai)
                            database.child("matakuliah").orderByChild("code").equalTo(nilai.mataKuliahId)  // Assuming the field is still mataKuliahId but contains code
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(mkSnapshot: DataSnapshot) {
                                        var mataKuliah: MataKuliah? = null
                                        for (mkChild in mkSnapshot.children) {
                                            mataKuliah = mkChild.getValue(MataKuliah::class.java)
                                            break
                                        }

                                        if (mataKuliah != null) {
                                            totalSks += mataKuliah.sks
                                        }

                                        processedCount++
                                        if (processedCount == totalNilaiCount) {
                                            val ipk = if (totalSks > 0) totalNilai / totalSks else 0.0
                                            val transkrip = TranskipItem(
                                                mahasiswaId,
                                                mahasiswaId,
                                                totalSks,
                                                ipk,
                                                nilaiList,
                                                System.currentTimeMillis()
                                            )

                                            database.child("transkrip").child(mahasiswaId).setValue(transkrip)
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        processedCount++
                                        if (processedCount == totalNilaiCount) {
                                            val ipk = if (totalSks > 0) totalNilai / totalSks else 0.0
                                            val transkrip = TranskipItem(
                                                mahasiswaId,
                                                mahasiswaId,
                                                totalSks,
                                                ipk,
                                                nilaiList,
                                                System.currentTimeMillis()
                                            )

                                            database.child("transkrip").child(mahasiswaId).setValue(transkrip)
                                        }
                                    }
                                })
                        }
                    }

                    if (totalNilaiCount == 0) {
                        val transkrip = TranskipItem(
                            mahasiswaId,
                            mahasiswaId,
                            0,
                            0.0,
                            nilaiList,
                            System.currentTimeMillis()
                        )
                        database.child("transkrip").child(mahasiswaId).setValue(transkrip)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun clearForm() {
        binding.etMahasiswaId.text.clear()
        binding.etMataKuliahId.text.clear()
        binding.etNilai.text.clear()
        binding.etSemester.text.clear()
        binding.etTahun.text.clear()
    }
}
package com.raffles.siasatuksw

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.raffles.siasatuksw.databinding.ActivityViewNilaiBinding
import com.google.firebase.database.*
import com.raffles.siasatuksw.model.Nilai
import com.raffles.siasatuksw.utilities.SessionManager

class ViewNilaiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewNilaiBinding
    private lateinit var database: DatabaseReference
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: NilaiAdapter
    private val nilaiList = mutableListOf<Nilai>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewNilaiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        database = FirebaseDatabase.getInstance("https://siasatuksw-c31ce-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
        sessionManager = SessionManager(this)

        setupRecyclerView()
        setupClickListeners()
        loadNilai()
    }

    private fun setupRecyclerView() {
        adapter = NilaiAdapter(nilaiList) { nilai ->
            // Handle item click if needed
        }
        binding.rvNilai.layoutManager = LinearLayoutManager(this)
        binding.rvNilai.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadNilai() {
        val user = sessionManager.getUser() ?: return

        database.child("nilai").orderByChild("mahasiswaId").equalTo(user.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    nilaiList.clear()
                    for (childSnapshot in snapshot.children) {
                        val nilai = childSnapshot.getValue(Nilai::class.java)
                        if (nilai != null) {
                            nilaiList.add(nilai)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ViewNilaiActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
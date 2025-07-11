package com.raffles.siasatuksw.utilities

import android.util.Log
import com.google.firebase.database.*
import com.raffles.siasatuksw.model.MataKuliah
import com.raffles.siasatuksw.model.Nilai
import com.raffles.siasatuksw.model.User
import kotlinx.coroutines.tasks.await

class FirebaseManager {
    private val database = FirebaseDatabase.getInstance("https://siasatuksw-c31ce-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val usersRef = database.getReference("users")
    private val mataKuliahRef = database.getReference("matakuliah")
    private val nilaiRef = database.getReference("nilai")

    suspend fun createUser(user: User): Boolean {
        return try {
            usersRef.child(user.id).setValue(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUser(userId: String): User? {
        return try {
            val snapshot = usersRef.child(userId).get().await()
            snapshot.getValue(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun loginUser(userId: String, password: String): User? {
        return try {
            val user = getUser(userId)
            if (user?.password == password) user else null
        } catch (e: Exception) {
            null
        }
    }

    // Mata Kuliah Operations
    suspend fun createMataKuliah(mataKuliah: MataKuliah): Boolean {
        return try {
            mataKuliahRef.child(mataKuliah.id).setValue(mataKuliah).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getMataKuliahByDosen(dosenId: String): List<MataKuliah> {
        return try {
            val snapshot = mataKuliahRef.orderByChild("dosenId").equalTo(dosenId).get().await()
            val mataKuliahList = snapshot.children.mapNotNull { it.getValue(MataKuliah::class.java) }

            mataKuliahList
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error fetching Mata Kuliah for Dosen ID: $dosenId", e)
            emptyList()
        }
    }

    suspend fun getAllMataKuliah(): List<MataKuliah> {
        return try {
            val snapshot = mataKuliahRef.get().await()
            snapshot.children.mapNotNull { it.getValue(MataKuliah::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun inputNilai(nilai: Nilai): Boolean {
        return try {
            nilaiRef.child(nilai.id).setValue(nilai).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getNilaiMahasiswa(mahasiswaId: String): List<Nilai> {
        return try {
            val snapshot = nilaiRef.orderByChild("mahasiswaId").equalTo(mahasiswaId).get().await()
            snapshot.children.mapNotNull { it.getValue(Nilai::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
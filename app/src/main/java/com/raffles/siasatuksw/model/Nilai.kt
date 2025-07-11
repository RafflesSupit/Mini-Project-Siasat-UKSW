package com.raffles.siasatuksw.model

data class Nilai(
    val id: String = "",
    val mahasiswaId: String = "",
    val mataKuliahId: String = "",
    val nilai: Double = 0.0,
    val grade: String = "",
    val semester: Int = 0,
    val tahunAkademik: String = "",
    val inputBy: String = "" // Dosen ID
)
package com.raffles.siasatuksw.model

data class TranskipItem(
    val id: String,
    val mahasiswaId: String,
    val totalSks: Int,
    val ipk: Double,
    val daftarNilai: List<Nilai>,
    val createdAt: Long
)

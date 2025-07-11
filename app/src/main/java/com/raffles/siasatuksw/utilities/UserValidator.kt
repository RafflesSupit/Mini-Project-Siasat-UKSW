package com.raffles.siasatuksw.utilities

object UserValidator {
    fun validateKodeDosen(kode: String): Boolean {
        return kode.length == 5 && kode.startsWith("67")
    }

    fun validateKodeMahasiswa(kode: String): Boolean {
        return kode.length in 9..10 && kode.all { it.isDigit() }
    }

    fun hitungGrade(nilai: Double): String {
        return when {
            nilai >= 85 -> "A"
            nilai >= 80 -> "A-"
            nilai >= 75 -> "B+"
            nilai >= 70 -> "B"
            nilai >= 65 -> "B-"
            nilai >= 60 -> "C+"
            nilai >= 55 -> "C"
            nilai >= 50 -> "C-"
            nilai >= 45 -> "D"
            else -> "E"
        }
    }

    fun hitungBobotNilai(grade: String): Double {
        return when (grade) {
            "A" -> 4.0
            "A-" -> 3.7
            "B+" -> 3.3
            "B" -> 3.0
            "B-" -> 2.7
            "C+" -> 2.3
            "C" -> 2.0
            "C-" -> 1.7
            "D" -> 1.0
            else -> 0.0
        }
    }
}
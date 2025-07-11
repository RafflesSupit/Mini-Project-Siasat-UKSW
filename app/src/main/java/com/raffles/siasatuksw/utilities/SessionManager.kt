package com.raffles.siasatuksw.utilities

import android.content.Context
import android.content.SharedPreferences
import com.raffles.siasatuksw.model.User

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("SiasatSession", Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        prefs.edit().apply {
            putString("user_id", user.id)
            putString("user_name", user.nama)
            putString("user_email", user.email)
            putString("user_role", user.userType)
            apply()
        }
    }

    fun getUser(): User? {
        val id = prefs.getString("user_id", null)
        val name = prefs.getString("user_name", null)
        val email = prefs.getString("user_email", null)
        val role = prefs.getString("user_role", null)

        return if (id != null && name != null && email != null && role != null) {
            User(id, name, email, role)
        } else null
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = prefs.getString("user_id", null) != null
}
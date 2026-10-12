package com.usj.internship.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // ================= STATE =================

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var registrationDone by mutableStateOf(false)
        private set

    var loginSuccess by mutableStateOf(false)
        private set

    var resetEmailSent by mutableStateOf(false)
        private set

    // "student", "supervisor" or "coordinator"
    var userRole by mutableStateOf("student")
        private set

    init {
        // App reopened while still signed in: load the profile
        if (isUserLoggedIn()) {
            loadProfile { }
        }
    }

    // ================= PROFILE (FIRESTORE) =================

    private fun loadProfile(onDone: () -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            Log.d("USJ_DB", "loadProfile: no signed-in user")
            onDone()
            return
        }

        Log.d("USJ_DB", "loadProfile: start for uid=${user.uid}")

        // Refresh the token so Firestore sees email_verified = true
        user.getIdToken(true).addOnCompleteListener {

            val ref = db.collection("users").document(user.uid)

            ref.get().addOnCompleteListener { task ->
                if (task.isSuccessful && task.result.exists()) {
                    Log.d("USJ_DB", "Profile found")
                    userRole = task.result.getString("role") ?: "student"
                    onDone()
                } else if (task.isSuccessful) {
                    Log.d("USJ_DB", "No profile yet, creating one")
                    // Role is always "student"; only the admin can change it
                    val profile = hashMapOf(
                        "fullName" to (user.displayName ?: ""),
                        "email" to (user.email ?: ""),
                        "role" to "student",
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                    ref.set(profile).addOnCompleteListener { setTask ->
                        if (setTask.isSuccessful) {
                            Log.d("USJ_DB", "Profile created OK")
                        } else {
                            Log.e("USJ_DB", "Profile create failed", setTask.exception)
                        }
                        userRole = "student"
                        onDone()
                    }
                } else {
                    Log.e("USJ_DB", "Profile load failed", task.exception)
                    userRole = "student" // least privilege by default
                    onDone()
                }
            }
        }
    }

    // ================= REGISTER =================

    fun register(fullName: String, email: String, password: String) {
        isLoading = true
        errorMessage = null

        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    isLoading = false
                    errorMessage = friendlyError(task.exception)
                    return@addOnCompleteListener
                }

                val user = auth.currentUser
                if (user == null) {
                    isLoading = false
                    errorMessage = "Something went wrong. Please try again."
                    return@addOnCompleteListener
                }

                val profile = UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName.trim())
                    .build()

                user.updateProfile(profile).addOnCompleteListener {
                    user.sendEmailVerification().addOnCompleteListener {
                        // Do not keep the user signed in until the email is verified
                        auth.signOut()
                        isLoading = false
                        registrationDone = true
                    }
                }
            }
    }

    // ================= LOGIN =================

    fun login(email: String, password: String) {
        isLoading = true
        errorMessage = null

        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    isLoading = false
                    errorMessage = if (task.exception is FirebaseNetworkException) {
                        "No internet connection. Please check your network."
                    } else {
                        // Same message for wrong email or wrong password (more secure)
                        "Invalid email or password."
                    }
                    return@addOnCompleteListener
                }

                val user = auth.currentUser
                if (user != null && user.isEmailVerified) {
                    loadProfile {
                        isLoading = false
                        loginSuccess = true
                    }
                } else {
                    user?.sendEmailVerification()
                    auth.signOut()
                    isLoading = false
                    errorMessage =
                        "Please verify your email first. We sent you a new link."
                }
            }
    }

    fun consumeLoginSuccess() {
        loginSuccess = false
    }

    // ================= FORGOT PASSWORD =================

    fun sendPasswordReset(email: String) {
        isLoading = true
        errorMessage = null

        auth.sendPasswordResetEmail(email.trim())
            .addOnCompleteListener { task ->
                isLoading = false

                if (task.exception is FirebaseNetworkException) {
                    errorMessage =
                        "No internet connection. Please check your network."
                } else {
                    if (!task.isSuccessful) {
                        Log.w("USJ_AUTH", "Password reset failed", task.exception)
                    }
                    // Same result whether or not the account exists,
                    // so nobody can discover which emails are registered.
                    resetEmailSent = true
                }
            }
    }

    fun resetForgotState() {
        resetEmailSent = false
        errorMessage = null
    }

    // ================= LOGOUT / SESSION =================

    fun logout() {
        auth.signOut()
        loginSuccess = false
        userRole = "student"
    }

    fun isUserLoggedIn(): Boolean {
        val user = auth.currentUser
        return user != null && user.isEmailVerified
    }

    fun currentUserName(): String =
        auth.currentUser?.displayName?.takeIf { it.isNotBlank() } ?: "Student"

    // ================= HELPERS =================

    fun clearError() {
        errorMessage = null
    }

    fun resetRegistration() {
        registrationDone = false
        errorMessage = null
    }

    private fun friendlyError(e: Exception?): String = when (e) {
        is FirebaseAuthUserCollisionException ->
            "An account with this email already exists."
        is FirebaseAuthWeakPasswordException ->
            "Password is too weak. Choose a stronger one."
        is FirebaseNetworkException ->
            "No internet connection. Please check your network."
        else -> {
            Log.e("USJ_AUTH", "Register failed", e)
            val code = (e as? FirebaseAuthException)?.errorCode
                ?: e?.javaClass?.simpleName
            "Registration failed ($code). ${e?.localizedMessage ?: ""}"
        }
    }
}
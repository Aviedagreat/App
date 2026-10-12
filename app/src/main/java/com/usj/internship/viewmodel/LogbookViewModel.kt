package com.usj.internship.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

data class LogbookEntry(
    val id: String,
    val date: String,
    val title: String,
    val activities: String,
    val hoursWorked: Double,
    val status: String,
    val supervisorComment: String
)

class LogbookViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var entries by mutableStateOf<List<LogbookEntry>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var saved by mutableStateOf(false)
        private set

    fun loadEntries() {
        val uid = auth.currentUser?.uid ?: return
        isLoading = true
        errorMessage = null

        db.collection("logbooks")
            .whereEqualTo("studentId", uid)
            .get()
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    entries = task.result.documents.map { doc ->
                        LogbookEntry(
                            id = doc.id,
                            date = doc.getString("date") ?: "",
                            title = doc.getString("title") ?: "",
                            activities = doc.getString("activities") ?: "",
                            hoursWorked = doc.getDouble("hoursWorked") ?: 0.0,
                            status = doc.getString("status") ?: "submitted",
                            supervisorComment = doc.getString("supervisorComment") ?: ""
                        )
                    }.sortedByDescending { it.date }
                } else {
                    Log.e("USJ_LOGBOOK", "Load failed", task.exception)
                    errorMessage = "Could not load your logbook. Check your connection."
                }
            }
    }

    fun addEntry(date: String, title: String, activities: String, hours: Double) {
        val user = auth.currentUser
        if (user == null) {
            errorMessage = "You are signed out. Please log in again."
            return
        }

        isLoading = true
        errorMessage = null

        val data = hashMapOf(
            "studentId" to user.uid,
            "studentName" to (user.displayName ?: ""),
            "date" to date.trim(),
            "title" to title.trim(),
            "activities" to activities.trim(),
            "hoursWorked" to hours,
            "status" to "submitted",
            "supervisorComment" to "",
            "createdAt" to FieldValue.serverTimestamp()
        )

        db.collection("logbooks")
            .add(data)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    saved = true
                    loadEntries()
                } else {
                    Log.e("USJ_LOGBOOK", "Save failed", task.exception)
                    errorMessage = "Could not save your entry. Please try again."
                }
            }
    }

    fun consumeSaved() {
        saved = false
    }

    fun clearError() {
        errorMessage = null
    }

    // Called on logout so the next user never sees old entries
    fun clear() {
        entries = emptyList()
        errorMessage = null
        saved = false
    }
}
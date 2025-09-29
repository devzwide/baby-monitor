package com.babymonitor.babyapp.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babymonitor.babyapp.models.Baby
import com.babymonitor.babyapp.models.Parent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserDataViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    var currentParent = mutableStateOf<Parent?>(null)
        private set

    var currentBaby = mutableStateOf<Baby?>(null)
        private set

    var isLoading = mutableStateOf(false)
        private set

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                loadUserData(user.uid)
            } else {
                clearUserData()
            }
        }
        auth.currentUser?.let { loadUserData(it.uid) }
    }

    private fun loadUserData(uid: String) {
        isLoading.value = true

        viewModelScope.launch {
            try {
                // Load parent data
                val parentDoc = db.collection("users").document(uid).get().await()
                if (parentDoc.exists()) {
                    val parent = parentDoc.toObject(Parent::class.java)
                    if (parent != null) {
                        currentParent.value = parent

                        // Load baby data using babyId from parent
                        if (parent.babyId.isNotEmpty()) {
                            val babyDoc = db.collection("babies").document(parent.babyId).get().await()
                            if (babyDoc.exists()) {
                                val baby = babyDoc.toObject(Baby::class.java)
                                if (baby != null) {
                                    currentBaby.value = baby
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun clearUserData() {
        currentParent.value = null
        currentBaby.value = null
        isLoading.value = false
    }

    fun getCurrentBabyId(): String? {
        return currentBaby.value?.babyID
    }

    fun refreshUserData() {
        auth.currentUser?.let { loadUserData(it.uid) }
    }
}
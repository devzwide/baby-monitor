package com.babymonitor.babyapp.controllers

import com.babymonitor.babyapp.data.FirebaseAuthManager
import com.babymonitor.babyapp.models.Baby
import com.babymonitor.babyapp.models.Parent
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AuthController(private val authManager: FirebaseAuthManager) {

    interface AuthCallback {
        fun onSuccess()
        fun onFailure(errorMessage: String)
    }

    fun handleSignUp(parent: Parent, baby: Baby, password: String, callback: AuthCallback) {
        // Input validation
        if (parent.email.isBlank() || password.isBlank() || password.length < 6 ||
            parent.name.isBlank() || parent.surname.isBlank() ||
            baby.name.isBlank() || baby.surname.isBlank() || baby.dateOfBirth.isBlank()) {
            callback.onFailure("All fields must be filled. Password must be at least 6 characters.")
            return
        }

        authManager.signUp(parent.email, password) { result ->
            result.onSuccess { user ->
                val db = FirebaseFirestore.getInstance()
                val userId = user.uid

                // Generate baby ID
                val babyId = db.collection("babies").document().id

                // Create parent with user ID and baby reference
                val parentWithId = parent.copy(
                    parentID = userId,
                    babyId = babyId
                )

                // Create baby with generated ID
                val babyWithId = baby.copy(babyID = babyId)

                // Save both documents
                val batch = db.batch()

                // Save parent under users collection
                val parentRef = db.collection("users").document(userId)
                batch.set(parentRef, parentWithId)

                // Save baby under babies collection
                val babyRef = db.collection("babies").document(babyId)
                batch.set(babyRef, babyWithId)

                batch.commit()
                    .addOnSuccessListener {
                        callback.onSuccess()
                    }
                    .addOnFailureListener { e ->
                        callback.onFailure(e.message ?: "Failed to save profile data.")
                    }
            }.onFailure { exception ->
                callback.onFailure(exception.message ?: "Sign Up failed")
            }
        }
    }

    fun handleSignIn(email: String, password: String, callback: AuthCallback) {
        if (email.isBlank() || password.isBlank()) {
            callback.onFailure("Email and Password must not be empty.")
            return
        }

        authManager.signIn(email, password) { result ->
            result.onSuccess {
                callback.onSuccess()
            }.onFailure { exception ->
                callback.onFailure(exception.message ?: "Sign In failed")
            }
        }
    }

    fun handleSignOut() {
        authManager.signOut()
    }

    fun isUserSignedIn(): Boolean {
        return authManager.getCurrentUser() != null
    }
}
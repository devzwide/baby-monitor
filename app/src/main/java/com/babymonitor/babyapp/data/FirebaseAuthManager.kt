package com.babymonitor.babyapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseAuthManager {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun signUp(email: String, password: String, callback: (Result<FirebaseUser>) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.user?.let { user ->
                    callback(Result.success(user))
                } ?: callback(Result.failure(Exception("Sign Up succeeded but user is null")))
            } else {
                callback(Result.failure(task.exception ?: Exception("Sign Up failed")))
            }
        }
    }

    fun signIn(email: String, password: String, callback: (Result<FirebaseUser>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.user?.let { user ->
                    callback(Result.success(user))
                } ?: callback(Result.failure(Exception("Sign In succeeded but user is null")))
            } else {
                callback(Result.failure(task.exception ?: Exception("Sign In failed")))
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}
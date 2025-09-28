package com.babymonitor.babyapp.models

data class Parent(
    val parentID: String = "",
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val role: String = "Parent",
    val isPrimaryGuardian: Boolean = true,
    val babyId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
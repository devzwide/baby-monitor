package com.babymonitor.babyapp.models

data class Baby(
    val babyID: String = "",
    val name: String = "",
    val surname: String = "",
    val dateOfBirth: String = "", // ISO format (yyyy-MM-dd)
    val gender: String = "Unknown",
    val weight: Double = 0.0,
    val height: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)
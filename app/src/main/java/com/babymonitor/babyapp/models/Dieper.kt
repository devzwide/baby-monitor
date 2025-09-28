package com.babymonitor.babyapp.models

data class Diaper(
    val entryID: String = "",
    val babyID: String = "",
    val timestamp: Long = 0,
    val type: String = "",
    val color: String? = null,
    val consistency: String? = null,
    val notes: String? = null
)

package com.babymonitor.babyapp.models

data class Sleep(
    val entryID: String = "",
    val babyID: String = "",
    val startTime: Long = 0,
    val endTime: Long? = null,
    val durationMinutes: Int = 0,
    val sleepLocation: String? = null,
    val isNap: Boolean = true,
    val notes: String? = null
)

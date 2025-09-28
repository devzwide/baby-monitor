package com.babymonitor.babyapp.models

data class Feeding (
    val entryID: String = "",
    val babyID: String = "",
    val timestamp: Long = 0,
    val feedingType: String = "",
    val side: String? = null,
    val amount: Double? = null,
    val amountUnit: String? = null,
    val durationMinutes: Int = 0,
    val notes: String? = null
)
package com.babymonitor.babyapp.models

data class Health(
    val entryID: String = "",
    val babyID: String = "",
    val timestamp: Long = 0,
    val metricType: String = "",
    val value: Double = 0.0,
    val unit: String = "",
    val medicationName: String? = null,
    val notes: String? = null
)

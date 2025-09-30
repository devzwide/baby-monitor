package com.babymonitor.babyapp.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babymonitor.babyapp.models.Diaper
import com.babymonitor.babyapp.models.Feeding
import com.babymonitor.babyapp.models.Health
import com.babymonitor.babyapp.models.Sleep
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActivityViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val feedings = mutableStateListOf<Feeding>()
    val sleeps = mutableStateListOf<Sleep>()
    val diapers = mutableStateListOf<Diaper>()
    val healthEntries = mutableStateListOf<Health>()

    // Suggestions based on activity data
    val suggestions = mutableStateListOf<String>()

    private var feedingListener: ListenerRegistration? = null
    private var sleepListener: ListenerRegistration? = null
    private var diaperListener: ListenerRegistration? = null
    private var healthListener: ListenerRegistration? = null
    private var scheduledListener: ListenerRegistration? = null

    private val _currentBabyId = MutableStateFlow<String?>(null)
    val currentBabyId: StateFlow<String?> = _currentBabyId

    // Remove encryption for simplicity - it's causing issues
    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                registerListenersForUser(user.uid)
            } else {
                removeAllListeners()
                clearLocalCaches()
            }
        }
        auth.currentUser?.let { registerListenersForUser(it.uid) }
    }

    fun setCurrentBabyId(babyId: String) {
        _currentBabyId.value = babyId
        // Restart listeners with new baby ID
        auth.currentUser?.let { registerListenersForUser(it.uid) }
    }

    private fun registerListenersForUser(uid: String) {
        removeAllListeners()

        val currentBabyId = _currentBabyId.value
        if (currentBabyId.isNullOrEmpty()) return

        // Feedings listener
        feedingListener = db.collection("users").document(uid)
            .collection("feedings")
            .whereEqualTo("babyID", currentBabyId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                feedings.clear()
                snapshot?.documents?.forEach { doc ->
                    val feeding = doc.toObject(Feeding::class.java)
                    if (feeding != null) {
                        feedings.add(feeding)
                    }
                }
                refreshData() // Refresh suggestions after updating feedings
            }

        // Sleeps listener
        sleepListener = db.collection("users").document(uid)
            .collection("sleeps")
            .whereEqualTo("babyID", currentBabyId)
            .orderBy("startTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                sleeps.clear()
                snapshot?.documents?.forEach { doc ->
                    val sleep = doc.toObject(Sleep::class.java)
                    if (sleep != null) {
                        sleeps.add(sleep)
                    }
                }
                refreshData() // Refresh suggestions after updating sleeps
            }

        // Diapers listener
        diaperListener = db.collection("users").document(uid)
            .collection("diapers")
            .whereEqualTo("babyID", currentBabyId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                diapers.clear()
                snapshot?.documents?.forEach { doc ->
                    val diaper = doc.toObject(Diaper::class.java)
                    if (diaper != null) {
                        diapers.add(diaper)
                    }
                }
                refreshData() // Refresh suggestions after updating diapers
            }

        // Health entries listener
        healthListener = db.collection("users").document(uid)
            .collection("health")
            .whereEqualTo("babyID", currentBabyId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                healthEntries.clear()
                snapshot?.documents?.forEach { doc ->
                    val health = doc.toObject(Health::class.java)
                    if (health != null) {
                        healthEntries.add(health)
                    }
                }
                refreshData() // Refresh suggestions after updating health entries
            }

        // Scheduled activities listener
        registerScheduledListener(uid)
    }

    private fun removeAllListeners() {
        feedingListener?.remove()
        sleepListener?.remove()
        diaperListener?.remove()
        healthListener?.remove()
        scheduledListener?.remove()
        feedingListener = null
        sleepListener = null
        diaperListener = null
        healthListener = null
        scheduledListener = null
    }

    private fun clearLocalCaches() {
        feedings.clear()
        sleeps.clear()
        diapers.clear()
        healthEntries.clear()
        _currentBabyId.value = null
    }

    // Simplified add functions without encryption
    fun addFeeding(feeding: Feeding, callback: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                if (user != null && _currentBabyId.value != null) {
                    val feedingWithIds = feeding.copy(
                        entryID = db.collection("users").document().id,
                        babyID = _currentBabyId.value!!
                    )
                    db.collection("users").document(user.uid)
                        .collection("feedings")
                        .document(feedingWithIds.entryID)
                        .set(feedingWithIds)
                        .addOnSuccessListener { callback(Result.success(Unit)) }
                        .addOnFailureListener { e -> callback(Result.failure(e)) }
                } else {
                    callback(Result.failure(Exception("User or baby ID missing")))
                }
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }

    fun addSleep(sleep: Sleep, callback: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                if (user != null && _currentBabyId.value != null) {
                    val sleepWithIds = sleep.copy(
                        entryID = db.collection("users").document().id,
                        babyID = _currentBabyId.value!!
                    )
                    db.collection("users").document(user.uid)
                        .collection("sleeps")
                        .document(sleepWithIds.entryID)
                        .set(sleepWithIds)
                        .addOnSuccessListener { callback(Result.success(Unit)) }
                        .addOnFailureListener { e -> callback(Result.failure(e)) }
                } else {
                    callback(Result.failure(Exception("User or baby ID missing")))
                }
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }

    fun addDiaper(diaper: Diaper, callback: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                if (user != null && _currentBabyId.value != null) {
                    val diaperWithIds = diaper.copy(
                        entryID = db.collection("users").document().id,
                        babyID = _currentBabyId.value!!,
                        timestamp = System.currentTimeMillis()
                    )
                    db.collection("users").document(user.uid)
                        .collection("diapers")
                        .document(diaperWithIds.entryID)
                        .set(diaperWithIds)
                        .addOnSuccessListener { callback(Result.success(Unit)); refreshAllData() }
                        .addOnFailureListener { e -> callback(Result.failure(e)) }
                } else {
                    callback(Result.failure(Exception("User or baby ID missing")))
                }
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }

    fun addHealth(health: Health, callback: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                if (user != null && _currentBabyId.value != null) {
                    val healthWithIds = health.copy(
                        entryID = db.collection("users").document().id,
                        babyID = _currentBabyId.value!!
                    )
                    db.collection("users").document(user.uid)
                        .collection("health")
                        .document(healthWithIds.entryID)
                        .set(healthWithIds)
                        .addOnSuccessListener { callback(Result.success(Unit)) }
                        .addOnFailureListener { e -> callback(Result.failure(e)) }
                } else {
                    callback(Result.failure(Exception("User or baby ID missing")))
                }
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }

    // Scheduling model for activities
    data class ScheduledActivity(
        val id: String = "",
        val babyID: String = "",
        val type: String = "", // feeding, sleep, diaper
        val scheduledTime: Long = 0L,
        val notes: String? = null
    )

    val scheduledActivities = mutableStateListOf<ScheduledActivity>()

    // Add a scheduled activity
    fun scheduleActivity(type: String, scheduledTime: Long, notes: String? = null, callback: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                val babyId = _currentBabyId.value
                if (user != null && babyId != null) {
                    val id = db.collection("users").document().id
                    val scheduled = ScheduledActivity(id, babyId, type, scheduledTime, notes)
                    db.collection("users").document(user.uid)
                        .collection("scheduledActivities")
                        .document(id)
                        .set(scheduled)
                        .addOnSuccessListener { callback(Result.success(Unit)); refreshScheduledActivities() }
                        .addOnFailureListener { e -> callback(Result.failure(e)) }
                } else {
                    callback(Result.failure(Exception("User or baby ID missing")))
                }
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }

    // Remove a scheduled activity
    fun removeScheduledActivity(id: String, callback: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                if (user != null) {
                    db.collection("users").document(user.uid)
                        .collection("scheduledActivities")
                        .document(id)
                        .delete()
                        .addOnSuccessListener { callback(Result.success(Unit)); refreshScheduledActivities() }
                        .addOnFailureListener { e -> callback(Result.failure(e)) }
                } else {
                    callback(Result.failure(Exception("User missing")))
                }
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }

    // Listener for scheduled activities
    private fun registerScheduledListener(uid: String) {
        scheduledListener?.remove()
        val babyId = _currentBabyId.value
        if (babyId.isNullOrEmpty()) return
        scheduledListener = db.collection("users").document(uid)
            .collection("scheduledActivities")
            .whereEqualTo("babyID", babyId)
            .orderBy("scheduledTime", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                scheduledActivities.clear()
                snapshot?.documents?.forEach { doc ->
                    val scheduled = doc.toObject(ScheduledActivity::class.java)
                    if (scheduled != null) scheduledActivities.add(scheduled)
                }
            }
    }

    // Refresh scheduled activities
    fun refreshScheduledActivities() {
        val user = auth.currentUser
        val babyId = _currentBabyId.value
        if (user != null && babyId != null) {
            db.collection("users").document(user.uid)
                .collection("scheduledActivities")
                .whereEqualTo("babyID", babyId)
                .orderBy("scheduledTime", com.google.firebase.firestore.Query.Direction.ASCENDING)
                .get().addOnSuccessListener { snapshot ->
                    scheduledActivities.clear()
                    snapshot.documents.forEach { doc ->
                        doc.toObject(ScheduledActivity::class.java)?.let { scheduledActivities.add(it) }
                    }
                }
        }
    }

    // Helper functions to get latest entries
    fun getLatestFeeding(): Feeding? = feedings.firstOrNull()
    fun getLatestSleep(): Sleep? = sleeps.firstOrNull()
    fun getLatestDiaper(): Diaper? = diapers.firstOrNull()
    fun getLatestHealth(): Health? = healthEntries.firstOrNull()

    // Suggestions generation logic
    private fun generateSuggestions() {
        suggestions.clear()
        if (feedings.size > 5) {
            suggestions.add("Your baby is feeding well! Keep up the routine.")
        }
        if (sleeps.size > 5) {
            suggestions.add("Great sleep pattern! Consistency helps.")
        }
        if (diapers.size > 5) {
            suggestions.add("Diaper changes are regular. Monitor for any changes.")
        }
        if (healthEntries.any { it.notes?.contains("fever", true) ?: false }) {
            suggestions.add("Monitor baby's temperature and consult a doctor if needed.")
        }
        if (suggestions.isEmpty()) {
            suggestions.add("Log more activities for personalized insights!")
        }
    }

    public val diaperColorStats = mutableStateOf<Map<String, Int>>(emptyMap())
    public val diaperConsistencyStats = mutableStateOf<Map<String, Int>>(emptyMap())
    public val diaperFrequency24h = mutableStateOf(0)

    private fun updateDiaperAnalytics() {
        val last24h = System.currentTimeMillis() - 24 * 60 * 60 * 1000
        val diapers24h = diapers.filter { it.timestamp > last24h }
        diaperFrequency24h.value = diapers24h.size
        diaperColorStats.value = diapers24h.groupingBy { it.color ?: "UNKNOWN" }.eachCount()
        diaperConsistencyStats.value = diapers24h.groupingBy { it.consistency ?: "UNKNOWN" }.eachCount()
    }

    // Call this after listeners update local lists
    private fun refreshData() {
        updateDiaperAnalytics()
        generateSuggestions()
        // ...other refresh logic if needed...
    }

    var isRefreshing = mutableStateOf(false)
        private set

    fun refreshAllData() {
        isRefreshing.value = true
        val user = auth.currentUser
        val babyId = _currentBabyId.value
        if (user != null && babyId != null) {
            // Re-attach Firestore listeners to ensure UI updates
            registerListenersForUser(user.uid)
            // Manually reload all activity data
            db.collection("users").document(user.uid).collection("feedings")
                .whereEqualTo("babyID", babyId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().addOnSuccessListener { snapshot ->
                    feedings.clear()
                    snapshot.documents.forEach { doc ->
                        doc.toObject(Feeding::class.java)?.let { feedings.add(it) }
                    }
                    refreshData()
                    isRefreshing.value = false
                }.addOnFailureListener {
                    isRefreshing.value = false
                }
            db.collection("users").document(user.uid).collection("sleeps")
                .whereEqualTo("babyID", babyId)
                .orderBy("startTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().addOnSuccessListener { snapshot ->
                    sleeps.clear()
                    snapshot.documents.forEach { doc ->
                        doc.toObject(Sleep::class.java)?.let { sleeps.add(it) }
                    }
                    refreshData()
                    isRefreshing.value = false
                }.addOnFailureListener {
                    isRefreshing.value = false
                }
            db.collection("users").document(user.uid).collection("diapers")
                .whereEqualTo("babyID", babyId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().addOnSuccessListener { snapshot ->
                    diapers.clear()
                    snapshot.documents.forEach { doc ->
                        doc.toObject(Diaper::class.java)?.let { diapers.add(it) }
                    }
                    refreshData()
                    isRefreshing.value = false
                }.addOnFailureListener {
                    isRefreshing.value = false
                }
            db.collection("users").document(user.uid).collection("health")
                .whereEqualTo("babyID", babyId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().addOnSuccessListener { snapshot ->
                    healthEntries.clear()
                    snapshot.documents.forEach { doc ->
                        doc.toObject(Health::class.java)?.let { healthEntries.add(it) }
                    }
                    refreshData()
                    isRefreshing.value = false
                }.addOnFailureListener {
                    isRefreshing.value = false
                }
        } else {
            isRefreshing.value = false
        }
    }

    override fun onCleared() {
        removeAllListeners()
        super.onCleared()
    }
}
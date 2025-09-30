package com.babymonitor.babyapp.views.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.babymonitor.babyapp.models.Diaper
import com.babymonitor.babyapp.models.Feeding
import com.babymonitor.babyapp.models.Health
import com.babymonitor.babyapp.models.Sleep
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    feedings: List<Feeding>,
    sleeps: List<Sleep>,
    diapers: List<Diaper>,
    healthEntries: List<Health>,
    suggestions: List<String>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy, h:mm a", Locale.getDefault()) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "BABY INSIGHTS",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    TextButton(onClick = onRefresh, enabled = !isRefreshing) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text("Refresh", fontSize = MaterialTheme.typography.titleLarge.fontSize)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isRefreshing) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Refreshing analytics...", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            Column(
                modifier = Modifier.padding(paddingValues).verticalScroll(rememberScrollState())
            ) {
                ActivityAnalyticsSummary(feedings, sleeps, diapers, healthEntries)
                // Instructions
                Card(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("ℹ️", fontSize = MaterialTheme.typography.titleLarge.fontSize)
                            Spacer(Modifier.width(8.dp))
                            Text("Tap refresh to update.", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                // Suggestions
                if (suggestions.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("💡 Personalized Suggestions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            suggestions.forEach { suggestion ->
                                Text("• $suggestion", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    }
                }
                // Activity Details Header
                Text(
                    text = "Activity Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
                // Feeding Activities
                Text(
                    text = "🍼 Feeding Activities",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                )
                if (feedings.isEmpty()) {
                    Row(Modifier.padding(start = 16.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("🍼", fontSize = MaterialTheme.typography.titleLarge.fontSize)
                        Spacer(Modifier.width(8.dp))
                        Text("No feeding records yet.", color = Color.Gray)
                    }
                } else {
                    feedings.forEach { feeding ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Type: ${feeding.feedingType}", fontWeight = FontWeight.Bold)
                                feeding.amount?.let { Text("Amount: $it ${feeding.amountUnit ?: ""}") }
                                feeding.side?.let { Text("Side: $it") }
                                Text("Duration: ${if (feeding.durationMinutes >= 60) "${feeding.durationMinutes/60}h ${feeding.durationMinutes%60}m" else "${feeding.durationMinutes} min"}")
                                Text("Time: ${dateFormat.format(Date(feeding.timestamp))}")
                                if (!feeding.notes.isNullOrBlank()) Text("Notes: ${feeding.notes}")
                            }
                        }
                    }
                }
                // Sleep Activities
                Text(
                    text = "😴 Sleep Activities",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                )
                if (sleeps.isEmpty()) {
                    Row(Modifier.padding(start = 16.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("😴", fontSize = MaterialTheme.typography.titleLarge.fontSize)
                        Spacer(Modifier.width(8.dp))
                        Text("No sleep records yet.", color = Color.Gray)
                    }
                } else {
                    sleeps.forEach { sleep ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Duration: ${if (sleep.durationMinutes >= 60) "${sleep.durationMinutes/60}h ${sleep.durationMinutes%60}m" else "${sleep.durationMinutes} min"}")
                                Text("Location: ${sleep.sleepLocation ?: "Unknown"}")
                                Text("Type: ${if (sleep.isNap) "Nap" else "Night"}")
                                sleep.endTime?.toLong()?.let { Text("Time: ${dateFormat.format(Date(it))}") }
                                if (!sleep.notes.isNullOrBlank()) Text("Notes: ${sleep.notes}")
                            }
                        }
                    }
                }
                // Diaper Activities
                Text(
                    text = "🧷 Diaper Activities",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                )
                if (diapers.isEmpty()) {
                    Row(Modifier.padding(start = 16.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("🧷", fontSize = MaterialTheme.typography.titleLarge.fontSize)
                        Spacer(Modifier.width(8.dp))
                        Text("No diaper records yet.", color = Color.Gray)
                    }
                } else {
                    diapers.forEach { diaper ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Type: ${diaper.type}", fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${diaper.color ?: "Unknown"}", modifier = Modifier.padding(end = 8.dp))
                                    Text("${diaper.consistency ?: "Unknown"}")
                                }
                                Text("Time: ${dateFormat.format(Date(diaper.timestamp))}")
                                if (!diaper.notes.isNullOrBlank()) Text("Notes: ${diaper.notes}")
                            }
                        }
                    }
                }
                // Health Activities
                Text(
                    text = "🩺 Health Activities",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                )
                if (healthEntries.isEmpty()) {
                    Row(Modifier.padding(start = 16.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("🩺", fontSize = MaterialTheme.typography.titleLarge.fontSize)
                        Spacer(Modifier.width(8.dp))
                        Text("No health records yet.", color = Color.Gray)
                    }
                } else {
                    healthEntries.forEach { health ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Type: ${health.metricType}", fontWeight = FontWeight.Bold)
                                Text("Value: ${health.value} ${health.unit}")
                                health.medicationName?.let { Text("Medication: $it") }
                                Text("Time: ${dateFormat.format(Date(health.timestamp))}")
                                if (!health.notes.isNullOrBlank()) Text("Notes: ${health.notes}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityAnalyticsSummary(
    feedings: List<Feeding>,
    sleeps: List<Sleep>,
    diapers: List<Diaper>,
    healthEntries: List<Health>
) {
    // Find earliest activity date
    val allTimestamps = (feedings.map { it.timestamp } + sleeps.map { it.endTime } + diapers.map { it.timestamp } + healthEntries.map { it.timestamp }).filterNotNull()
    val minTimestamp = allTimestamps.minOrNull() ?: System.currentTimeMillis()
    val today = Calendar.getInstance().apply { timeInMillis = System.currentTimeMillis() }
    val startDay = Calendar.getInstance().apply { timeInMillis = minTimestamp }
    val days = ((today.timeInMillis - startDay.timeInMillis) / (24 * 60 * 60 * 1000L)).toInt() + 1
    val millisInDay = 24 * 60 * 60 * 1000L
    val dayLabels = (0 until days).map { i ->
        val date = Date(startDay.timeInMillis + i * millisInDay)
        SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
    }
    val feedingCounts = (0 until days).map { i ->
        val start = startDay.timeInMillis + i * millisInDay
        val end = start + millisInDay
        feedings.count { it.timestamp in start until end }
    }
    val sleepCounts = (0 until days).map { i ->
        val start = startDay.timeInMillis + i * millisInDay
        val end = start + millisInDay
        sleeps.count { it.endTime in start until end }
    }
    val diaperCounts = (0 until days).map { i ->
        val start = startDay.timeInMillis + i * millisInDay
        val end = start + millisInDay
        diapers.count { it.timestamp in start until end }
    }
    val healthCounts = (0 until days).map { i ->
        val start = startDay.timeInMillis + i * millisInDay
        val end = start + millisInDay
        healthEntries.count { it.timestamp in start until end }
    }
    val maxCount = listOf(feedingCounts, sleepCounts, diaperCounts, healthCounts).flatten().maxOrNull()?.coerceAtLeast(1) ?: 1
    Column(Modifier.fillMaxWidth().padding(12.dp)) {
        Text("📊 Activity Analytics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        // Color legend
        Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(16.dp).background(Color(0xFF90CAF9)))
                Spacer(Modifier.width(4.dp))
                Text("🍼 Feeding", style = MaterialTheme.typography.labelMedium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(16.dp).background(Color(0xFFA5D6A7)))
                Spacer(Modifier.width(4.dp))
                Text("😴 Sleep", style = MaterialTheme.typography.labelMedium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(16.dp).background(Color(0xFFFFF59D)))
                Spacer(Modifier.width(4.dp))
                Text("🧷 Diaper", style = MaterialTheme.typography.labelMedium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(16.dp).background(Color(0xFFCE93D8)))
                Spacer(Modifier.width(4.dp))
                Text("🩺 Health", style = MaterialTheme.typography.labelMedium)
            }
        }
        // Chart
        for (i in 0 until days) {
            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(dayLabels[i], modifier = Modifier.width(70.dp), style = MaterialTheme.typography.labelSmall)
                // Feeding bar
                Box(
                    Modifier
                        .height(16.dp)
                        .width((feedingCounts[i] * 40 / maxCount).dp)
                        .background(Color(0xFF90CAF9)),
                )
                Spacer(Modifier.width(4.dp))
                // Sleep bar
                Box(
                    Modifier
                        .height(16.dp)
                        .width((sleepCounts[i] * 40 / maxCount).dp)
                        .background(Color(0xFFA5D6A7)),
                )
                Spacer(Modifier.width(4.dp))
                // Diaper bar
                Box(
                    Modifier
                        .height(16.dp)
                        .width((diaperCounts[i] * 40 / maxCount).dp)
                        .background(Color(0xFFFFF59D)),
                )
                Spacer(Modifier.width(4.dp))
                // Health bar
                Box(
                    Modifier
                        .height(16.dp)
                        .width((healthCounts[i] * 40 / maxCount).dp)
                        .background(Color(0xFFCE93D8)),
                )
                Spacer(Modifier.width(8.dp))
                Text("${feedingCounts[i]}/${sleepCounts[i]}/${diaperCounts[i]}/${healthCounts[i]}", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun AverageStat(label: String, value: String, textColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(min = 80.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = textColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ActivitySummaryItem(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun SuggestionItem(text: String, number: Int, color: Color) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Number indicator instead of icon
            Text(
                text = "$number",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Suppress("unused")
fun generateSuggestions(feedings: List<Feeding>, sleeps: List<Sleep>, diapers: List<Diaper>, healthEntries: List<Health>): List<String> {
    return listOf(
        "Establish a consistent bedtime routine to help your baby sleep better",
        "Track feeding patterns - newborns typically eat every 2-3 hours",
        "Monitor diaper output - it indicates proper hydration and nutrition",
        "Keep room temperature between 68-70°F for optimal sleep comfort",
        "Consider white noise or gentle lullabies to soothe your baby",
        "Document any concerning symptoms and discuss with your pediatrician",
        "Take care of yourself too - rest when the baby sleeps",
        "Burp your baby frequently during feedings to reduce discomfort",
        "Watch for growth spurts which may temporarily change eating patterns",
        "Create a calm environment during feeding and sleep times"
    )
}
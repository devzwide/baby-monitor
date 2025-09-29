package com.babymonitor.babyapp.views.screens

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
                    // Refresh button without icon
                    TextButton(
                        onClick = onRefresh,
                        enabled = !isRefreshing
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        } else {
                            Text(
                                "REFRESH",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        // Show loading indicator when refreshing
        if (isRefreshing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 4.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Refreshing analytics...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Section
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Activity Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Feeding Activities
                    Text(
                        text = "Feeding Activities",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    if (feedings.isEmpty()) {
                        Text("No feeding records.", color = Color.Gray)
                    } else {
                        feedings.forEach { feeding ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text("Type: ${feeding.feedingType}", fontWeight = FontWeight.Bold)
                                    feeding.amount?.let { Text("Amount: $it ${feeding.amountUnit ?: ""}") }
                                    feeding.side?.let { Text("Side: $it") }
                                    Text("Duration: ${feeding.durationMinutes} min")
                                    Text("Notes: ${feeding.notes ?: "None"}")
                                }
                            }
                        }
                    }

                    // Sleep Activities
                    Text(
                        text = "Sleep Activities",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    if (sleeps.isEmpty()) {
                        Text("No sleep records.", color = Color.Gray)
                    } else {
                        sleeps.forEach { sleep ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text("Duration: ${sleep.durationMinutes} min")
                                    Text("Location: ${sleep.sleepLocation ?: "Unknown"}")
                                    Text("Type: ${if (sleep.isNap) "Nap" else "Night"}")
                                    Text("Notes: ${sleep.notes ?: "None"}")
                                }
                            }
                        }
                    }

                    // Diaper Activities
                    Text(
                        text = "Diaper Activities",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    if (diapers.isEmpty()) {
                        Text("No diaper records.", color = Color.Gray)
                    } else {
                        diapers.forEach { diaper ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text("Type: ${diaper.type}", fontWeight = FontWeight.Bold)
                                    Text("Color: ${diaper.color ?: "Unknown"}")
                                    Text("Consistency: ${diaper.consistency ?: "Unknown"}")
                                    Text("Notes: ${diaper.notes ?: "None"}")
                                }
                            }
                        }
                    }

                    // Health Activities
                    Text(
                        text = "Health Activities",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    if (healthEntries.isEmpty()) {
                        Text("No health records.", color = Color.Gray)
                    } else {
                        healthEntries.forEach { health ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text("Type: ${health.metricType}", fontWeight = FontWeight.Bold)
                                    Text("Value: ${health.value} ${health.unit}")
                                    health.medicationName?.let { Text("Medication: $it") }
                                    Text("Notes: ${health.notes ?: "None"}")
                                }
                            }
                        }
                    }
                }

                // Averages Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            "DAILY AVERAGES",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            AverageStat(
                                label = "SLEEP",
                                value = "${if (sleeps.isNotEmpty()) sleeps.map { it.durationMinutes }.average().toInt() else 0} min",
                                textColor = MaterialTheme.colorScheme.primary
                            )
                            AverageStat(
                                label = "FEEDINGS",
                                value = "${feedings.size}",
                                textColor = MaterialTheme.colorScheme.secondary
                            )
                            AverageStat(
                                label = "DIAPERS",
                                value = "${diapers.size}",
                                textColor = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Recent Activity Section (replaces chart)
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Text(
                        "RECENT ACTIVITY SUMMARY",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                "LAST 7 DAYS SUMMARY",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Sleep summary
                            ActivitySummaryItem(
                                label = "TOTAL SLEEP TIME",
                                value = "${sleeps.sumOf { it.durationMinutes } / 60}h ${sleeps.sumOf { it.durationMinutes } % 60}m",
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Feeding summary
                            ActivitySummaryItem(
                                label = "TOTAL FEEDINGS",
                                value = "${feedings.size}",
                                color = MaterialTheme.colorScheme.secondary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Diaper summary
                            ActivitySummaryItem(
                                label = "DIAPER CHANGES",
                                value = "${diapers.size}",
                                color = MaterialTheme.colorScheme.tertiary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Health summary
                            ActivitySummaryItem(
                                label = "HEALTH CHECKS",
                                value = "${healthEntries.size}",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Suggestions Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        "PERSONALIZED SUGGESTIONS",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val suggestionsToShow = if (suggestions.isNotEmpty()) suggestions else generateSuggestions(feedings, sleeps, diapers, healthEntries)

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        suggestionsToShow.take(5).forEachIndexed { index, suggestion ->
                            SuggestionItem(
                                text = suggestion,
                                number = index + 1,
                                color = when (index % 3) {
                                    0 -> MaterialTheme.colorScheme.primary
                                    1 -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.tertiary
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
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
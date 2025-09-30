package com.babymonitor.babyapp.views.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.babymonitor.babyapp.models.Diaper
import com.babymonitor.babyapp.models.Feeding
import com.babymonitor.babyapp.models.Health
import com.babymonitor.babyapp.models.Sleep

@Composable
fun TrackingScreen(
    onSaveFeeding: (Feeding) -> Unit,
    onSaveSleep: (Sleep) -> Unit,
    onSaveDiaper: (Diaper) -> Unit,
    onSaveHealth: (Health) -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("FEEDING", "SLEEP", "DIAPER", "HEALTH")

    Column(modifier = Modifier.fillMaxSize()) {
        // Enhanced Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 24.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Content for each tab
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            when (selectedTabIndex) {
                0 -> FeedingLogger(onSaveFeeding)
                1 -> SleepLogger(onSaveSleep)
                2 -> DiaperLogger(onSaveDiaper)
                3 -> HealthLogger(onSaveHealth)
            }
        }
    }
}

@Composable
fun FeedingLogger(onSave: (Feeding) -> Unit) {
    var notes by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("BOTTLE") }
    var duration by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "LOG FEEDING",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Feeding Type Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "FEEDING TYPE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    FilterChip(
                        selected = type == "BOTTLE",
                        onClick = { type = "BOTTLE" },
                        label = { Text("BOTTLE") },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    FilterChip(
                        selected = type == "BREAST",
                        onClick = { type = "BREAST" },
                        label = { Text("BREAST") },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        // Amount Input
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("AMOUNT (ML)", fontWeight = FontWeight.Medium) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Duration Input
        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text("DURATION (MINUTES)", fontWeight = FontWeight.Medium) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Notes Input
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("NOTES (OPTIONAL)", fontWeight = FontWeight.Medium) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Save Button
        Button(
            onClick = {
                val feeding = Feeding(
                    feedingType = type,
                    amount = amount.toDoubleOrNull(),
                    amountUnit = "ml",
                    durationMinutes = duration.toIntOrNull() ?: 0,
                    notes = notes
                )
                onSave(feeding)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("SAVE FEEDING", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DiaperLogger(onSave: (Diaper) -> Unit) {
    var notes by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("WET") }
    var color by remember { mutableStateOf("") }
    var consistency by remember { mutableStateOf("") }
    var shouldReset by remember { mutableStateOf(false) }

    if (shouldReset) {
        LaunchedEffect(Unit) {
            notes = ""
            type = "WET"
            color = ""
            consistency = ""
            shouldReset = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "LOG DIAPER CHANGE",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary
        )

        // Diaper Type Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "DIAPER TYPE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("WET", "DIRTY", "MIXED").forEach { diaperType ->
                        FilterChip(
                            selected = type == diaperType,
                            onClick = { type = diaperType },
                            label = { Text(diaperType) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.tertiary,
                                selectedLabelColor = MaterialTheme.colorScheme.onTertiary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        if (diaperType != "MIXED") Spacer(modifier = Modifier.width(12.dp))
                    }
                }
            }
        }

        // Color Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "COLOR",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("YELLOW", "BROWN", "GREEN", "OTHER").forEach { option ->
                        FilterChip(
                            selected = color == option,
                            onClick = { color = option },
                            label = { Text(option) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.tertiary,
                                selectedLabelColor = MaterialTheme.colorScheme.onTertiary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        if (option != "OTHER") Spacer(modifier = Modifier.width(12.dp))
                    }
                }
            }
        }

        // Consistency Selection (only for dirty/mixed)
        if (type != "WET") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "CONSISTENCY",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("SOLID", "SOFT", "LOOSE", "WATERY").forEach { option ->
                            FilterChip(
                                selected = consistency == option,
                                onClick = { consistency = option },
                                label = { Text(option) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.tertiary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onTertiary,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            if (option != "WATERY") Spacer(modifier = Modifier.width(12.dp))
                        }
                    }
                }
            }
        }

        // Notes Input
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("NOTES (OPTIONAL)", fontWeight = FontWeight.Medium) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Save Button
        Button(
            onClick = {
                onSave(
                    Diaper(
                        type = type,
                        color = color.takeIf { it.isNotEmpty() },
                        consistency = consistency.takeIf { it.isNotEmpty() },
                        notes = notes.takeIf { it.isNotEmpty() },
                        timestamp = System.currentTimeMillis()
                    )
                )
                shouldReset = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            )
        ) {
            Text(
                "SAVE DIAPER RECORD",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SleepLogger(onSave: (Sleep) -> Unit) {
    var notes by remember { mutableStateOf("") }
    var durationHours by remember { mutableStateOf("") }
    var durationMinutes by remember { mutableStateOf("") }
    var sleepLocation by remember { mutableStateOf("CRIB") }
    var isNap by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "LOG SLEEP SESSION",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        // Sleep Type Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "SLEEP TYPE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    FilterChip(
                        selected = isNap,
                        onClick = { isNap = true },
                        label = { Text("NAP") },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    FilterChip(
                        selected = !isNap,
                        onClick = { isNap = false },
                        label = { Text("NIGHT SLEEP") },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        // Sleep Location Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "SLEEP LOCATION",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("CRIB", "BED", "OTHER").forEach { location ->
                        FilterChip(
                            selected = sleepLocation == location,
                            onClick = { sleepLocation = location },
                            label = { Text(location) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        if (location != "OTHER") Spacer(modifier = Modifier.width(12.dp))
                    }
                }
            }
        }

        // Duration Input
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "SLEEP DURATION",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = durationHours,
                        onValueChange = { durationHours = it },
                        label = { Text("HOURS", fontWeight = FontWeight.Medium) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    OutlinedTextField(
                        value = durationMinutes,
                        onValueChange = { durationMinutes = it },
                        label = { Text("MINUTES", fontWeight = FontWeight.Medium) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        }

        // Notes Input
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("NOTES (OPTIONAL)", fontWeight = FontWeight.Medium) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Save Button
        Button(
            onClick = {
                val totalMinutes = (durationHours.toIntOrNull() ?: 0) * 60 + (durationMinutes.toIntOrNull() ?: 0)
                val currentTime = System.currentTimeMillis()
                onSave(Sleep(
                    startTime = currentTime - (totalMinutes * 60 * 1000L),
                    endTime = currentTime,
                    durationMinutes = totalMinutes,
                    sleepLocation = sleepLocation,
                    isNap = isNap,
                    notes = notes
                ))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text(
                "SAVE SLEEP RECORD",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HealthLogger(onSave: (Health) -> Unit) {
    var notes by remember { mutableStateOf("") }
    var metricType by remember { mutableStateOf("TEMPERATURE") }
    var value by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("°C") }
    var medicationName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "LOG HEALTH METRIC",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )

        // Metric Type Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "METRIC TYPE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("TEMPERATURE", "WEIGHT", "HEIGHT").forEach { metric ->
                        FilterChip(
                            selected = metricType == metric,
                            onClick = {
                                metricType = metric
                                unit = when (metric) {
                                    "TEMPERATURE" -> "°C"
                                    "WEIGHT" -> "kg"
                                    "HEIGHT" -> "cm"
                                    else -> ""
                                }
                            },
                            label = { Text(metric) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.error,
                                selectedLabelColor = MaterialTheme.colorScheme.onError,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        if (metric != "HEIGHT") Spacer(modifier = Modifier.width(12.dp))
                    }
                }
            }
        }

        // Value Input
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "MEASUREMENT",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = { value = it },
                        label = { Text("VALUE", fontWeight = FontWeight.Medium) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.error,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("UNIT", fontWeight = FontWeight.Medium) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.error,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        }

        // Medication Input
        OutlinedTextField(
            value = medicationName,
            onValueChange = { medicationName = it },
            label = { Text("MEDICATION (OPTIONAL)", fontWeight = FontWeight.Medium) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.error,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Notes Input
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("NOTES (OPTIONAL)", fontWeight = FontWeight.Medium) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.error,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Save Button
        Button(
            onClick = {
                onSave(Health(
                    timestamp = System.currentTimeMillis(),
                    metricType = metricType,
                    value = value.toDoubleOrNull() ?: 0.0,
                    unit = unit,
                    medicationName = medicationName.ifBlank { null },
                    notes = notes.ifBlank { null }
                ))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text(
                "SAVE HEALTH RECORD",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
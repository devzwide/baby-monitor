package com.babymonitor.babyapp.views.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.babymonitor.babyapp.models.Baby
import com.babymonitor.babyapp.models.Diaper
import com.babymonitor.babyapp.models.Feeding
import com.babymonitor.babyapp.models.Health
import com.babymonitor.babyapp.models.Parent
import com.babymonitor.babyapp.models.Sleep
import com.babymonitor.babyapp.viewmodels.ActivityViewModel

// Data class for the summary grid items
data class SummaryItem(
    val title: String,
    val value: String,
    val backgroundColor: Color,
    val textColor: Color
)

data class AnalyticalSummary(
    val label: String,
    val value: String,
    val details: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    parent: Parent,
    baby: Baby,
    latestFeeding: Feeding?,
    latestSleep: Sleep?,
    latestDiaper: Diaper?,
    latestHealth: Health?,
    onRecordNewActivity: () -> Unit,
    onViewReports: () -> Unit,
    onLogout: () -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
) {
    // Manual refresh only, removed auto-refresh

    val summaryItems = listOf(
        SummaryItem(
            title = "LAST FEED",
            value = if (latestFeeding != null) "The baby has been fed" else "NO DATA",
            backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            textColor = MaterialTheme.colorScheme.primary
        ),
        SummaryItem(
            title = "LAST SLEEP",
            value = latestSleep?.let { timeAgo(it.startTime) } ?: "NO DATA",
            backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
            textColor = MaterialTheme.colorScheme.secondary
        ),
        SummaryItem(
            title = "LAST DIAPER",
            value = latestDiaper?.let { timeAgo(it.timestamp) } ?: "NO DATA",
            backgroundColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
            textColor = MaterialTheme.colorScheme.tertiary
        ),
        SummaryItem(
            title = "LAST CHECK",
            value = latestHealth?.let { timeAgo(it.timestamp) } ?: "NO DATA",
            backgroundColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
            textColor = MaterialTheme.colorScheme.error
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hello, ${parent.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${baby.name}'s Monitor",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
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
                    TextButton(onClick = onLogout) {
                        Text(
                            "LOGOUT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            // Icon-free FAB implementation
            FilledTonalButton(
                onClick = onRecordNewActivity,
                modifier = Modifier
                    .height(56.dp)
                    .widthIn(min = 200.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    "RECORD ACTIVITY",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Spacer(Modifier.weight(1f))
                TextButton(
                    onClick = onViewReports,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        "VIEW REPORTS",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(Modifier.weight(1f))
            }
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
                        "Refreshing data...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                // Main monitoring section
                MonitorStatusSection(babyName = baby.name)

                Spacer(Modifier.height(32.dp))

                // Metrics section
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "RECENT ACTIVITY",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Enhanced grid layout
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(summaryItems) { item ->
                            MetricCard(item)
                        }
                    }
                }

                // DiaperAnalyticsSection(viewModel) // Uncomment when ViewModel is available
            }
        }
    }
}

@Composable
fun MonitorStatusSection(babyName: String) {
    val babyImages = remember {
        listOf(
            "https://img.freepik.com/free-photo/adorable-baby-lying-bed-smiling_181624-41180.jpg?semt=ais_hybrid&w=740&q=80", // Baby 1
            "https://img.freepik.com/free-photo/portrait-young-family_1328-3806.jpg?semt=ais_hybrid&w=740&q=80", // Baby 2
            "https://img.freepik.com/free-photo/newborn-babyboy-cute-little-resting-baby-with-grey-hat-grey-toy-bear-his-hand-pacifier-his-mouth-white-floor_179666-157.jpg?semt=ais_hybrid&w=740&q=80", // Baby 3
            "https://img.freepik.com/free-vector/cute-baby-sleeping-cloud-pillow-cartoon-icon-illustration_138676-2812.jpg?semt=ais_hybrid&w=740&q=80", // Baby 4
            "https://img.freepik.com/free-photo/cute-black-baby-portrait-home_23-2149618887.jpg?semt=ais_hybrid&w=740&q=80", // Baby 5
            "https://img.freepik.com/free-photo/sleeping-infant-peacefully-laying-little-newborn-with-cute-grey-hat-toy-bear_179666-160.jpg?semt=ais_hybrid&w=740&q=80", // Baby 6
            "https://img.freepik.com/premium-photo/portrait-cute-boy-lying-bed-home_1048944-7423682.jpg?semt=ais_hybrid&w=740&q=80", // Baby 7
            "https://img.freepik.com/free-photo/black-mother-taking-car-her-child_23-2149836834.jpg?semt=ais_hybrid&w=740&q=80", // Baby 8
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .height(220.dp), // Increased height for better image display
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Baby Photo Gallery",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Horizontal scrolling image gallery
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(babyImages) { imageUrl ->
                    Card(
                        modifier = Modifier
                            .width(140.dp)
                            .fillMaxHeight(),
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Baby Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Text(
                text = "Swipe to see more →",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun MetricCard(item: SummaryItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = item.backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Category label
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = item.textColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Value with emphasis
            Text(
                text = item.value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Subtle status indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        color = item.textColor.copy(alpha = 0.3f),
                        shape = MaterialTheme.shapes.small
                    )
            )
        }
    }
}

@Composable
fun AnalyticalMetricCard(
    item: SummaryItem,
    analytics: AnalyticalSummary
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = item.backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = item.textColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = item.value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = analytics.label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = item.textColor
            )
            Text(
                text = analytics.value,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            analytics.details.forEach {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        color = item.textColor.copy(alpha = 0.3f),
                        shape = MaterialTheme.shapes.small
                    )
            )
        }
    }
}

// Utility function remains the same
@Composable
fun timeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = diff / 60000
    if (minutes < 1) return "Just now"
    if (minutes < 60) return "$minutes min ago"
    val hours = minutes / 60
    if (hours < 24) return "$hours hr ago"
    return "${hours / 24} days ago"
}

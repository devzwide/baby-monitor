package com.babymonitor.babyapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.babymonitor.babyapp.models.Baby
import com.babymonitor.babyapp.models.Parent
import com.babymonitor.babyapp.services.AdviceReminderWorker
import com.babymonitor.babyapp.ui.theme.BabymonitorTheme
import com.babymonitor.babyapp.viewmodels.AuthViewModel
import com.babymonitor.babyapp.viewmodels.ActivityViewModel
import com.babymonitor.babyapp.viewmodels.UserDataViewModel
import com.babymonitor.babyapp.views.screens.LoginScreen
import com.babymonitor.babyapp.views.screens.SignUpScreen
import com.babymonitor.babyapp.views.screens.HomeScreen
import com.babymonitor.babyapp.views.screens.TrackingScreen
import com.babymonitor.babyapp.views.screens.ReportsScreen
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private fun showActivityNotification(context: Context, title: String, message: String) {
        val channelId = "activity_created_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Activity Created Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notificationBuilder = androidx.core.app.NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        notificationManager.notify((System.currentTimeMillis() / 1000).toInt(), notificationBuilder.build())
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()

        // Schedule advice/reminder notifications every minute for demo
        val workRequest = PeriodicWorkRequestBuilder<AdviceReminderWorker>(1, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)

        setContent {
            BabymonitorTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val activityViewModel: ActivityViewModel = viewModel()
                val userDataViewModel: UserDataViewModel = viewModel()
                val isSignedIn = authViewModel.isUserSignedIn()
                val parent by userDataViewModel.currentParent
                val baby by userDataViewModel.currentBaby

                // Ensure ActivityViewModel has the current baby ID
                LaunchedEffect(baby?.babyID) {
                    baby?.babyID?.let { activityViewModel.setCurrentBabyId(it) }
                }

                NavHost(
                    navController = navController,
                    startDestination = if (isSignedIn) "home" else "login"
                ) {
                    composable("login") {
                        LoginScreen(
                            authViewModel = authViewModel,
                            onNavigateToHome = { navController.navigate("home") { popUpTo("login") { inclusive = true } } },
                            onNavigateToSignUp = { navController.navigate("signup") }
                        )
                    }
                    composable("signup") {
                        SignUpScreen(
                            authViewModel = authViewModel,
                            onNavigateToHome = { navController.navigate("home") { popUpTo("signup") { inclusive = true } } },
                            onNavigateToLogin = { navController.navigate("login") { popUpTo("signup") { inclusive = true } } }
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            parent = parent ?: Parent(),
                            baby = baby ?: Baby(),
                            latestFeeding = activityViewModel.getLatestFeeding(),
                            latestSleep = activityViewModel.getLatestSleep(),
                            latestDiaper = activityViewModel.getLatestDiaper(),
                            latestHealth = activityViewModel.getLatestHealth(),
                            onRecordNewActivity = { navController.navigate("tracking") },
                            onViewReports = { navController.navigate("reports") },
                            onLogout = {
                                authViewModel.signOut()
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            isRefreshing = activityViewModel.isRefreshing.value,
                            onRefresh = {
                                activityViewModel.refreshAllData()
                                userDataViewModel.refreshUserData()
                            }
                        )
                    }
                    composable("tracking") {
                        TrackingScreen(
                            onSaveFeeding = { feeding ->
                                activityViewModel.addFeeding(feeding) { result ->
                                    result.onSuccess {
                                        showActivityNotification(
                                            this@MainActivity,
                                            "Successfully created Feeding Activity",
                                            "Successfully created Feeding Activity"
                                        )
                                        navController.popBackStack()
                                    }
                                    result.onFailure { /* TODO: Show error message to user */ }
                                }
                            },
                            onSaveSleep = { sleep ->
                                activityViewModel.addSleep(sleep) { result ->
                                    result.onSuccess {
                                        showActivityNotification(
                                            this@MainActivity,
                                            "Successfully created Sleep Activity",
                                            "Successfully created Sleep Activity"
                                        )
                                        navController.popBackStack()
                                    }
                                    result.onFailure { /* TODO: Show error message to user */ }
                                }
                            },
                            onSaveDiaper = { diaper ->
                                activityViewModel.addDiaper(diaper) { result ->
                                    result.onSuccess {
                                        showActivityNotification(
                                            this@MainActivity,
                                            "Successfully created Diaper Activity",
                                            "Successfully created Diaper Activity"
                                        )
                                        navController.popBackStack()
                                    }
                                    result.onFailure { /* TODO: Show error message to user */ }
                                }
                            },
                            onSaveHealth = { health ->
                                activityViewModel.addHealth(health) { result ->
                                    result.onSuccess {
                                        showActivityNotification(
                                            this@MainActivity,
                                            "Successfully created Health Activity",
                                            "Successfully created Health Activity"
                                        )
                                        navController.popBackStack()
                                    }
                                    result.onFailure { /* TODO: Show error message to user */ }
                                }
                            }
                        )
                    }
                    composable("reports") {
                        ReportsScreen(
                            feedings = activityViewModel.feedings,
                            sleeps = activityViewModel.sleeps,
                            diapers = activityViewModel.diapers,
                            healthEntries = activityViewModel.healthEntries,
                            suggestions = activityViewModel.suggestions,
                            isRefreshing = activityViewModel.isRefreshing.value,
                            onRefresh = {
                                activityViewModel.refreshAllData()
                                userDataViewModel.refreshUserData()
                            }
                        )
                    }
                }
            }
        }
    }
}
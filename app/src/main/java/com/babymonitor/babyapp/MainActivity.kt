package com.babymonitor.babyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.babymonitor.babyapp.models.Baby
import com.babymonitor.babyapp.models.Parent
import com.babymonitor.babyapp.ui.theme.BabymonitorTheme
import com.babymonitor.babyapp.viewmodels.AuthViewModel
import com.babymonitor.babyapp.viewmodels.ActivityViewModel
import com.babymonitor.babyapp.viewmodels.UserDataViewModel
import com.babymonitor.babyapp.views.screens.LoginScreen
import com.babymonitor.babyapp.views.screens.SignUpScreen
import com.babymonitor.babyapp.views.screens.HomeScreen
import com.babymonitor.babyapp.views.screens.TrackingScreen
import com.babymonitor.babyapp.views.screens.ReportsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                            onRefresh = { activityViewModel.refreshAllData() }
                        )
                    }
                    composable("tracking") {
                        TrackingScreen(
                            onSaveFeeding = { feeding ->
                                activityViewModel.addFeeding(feeding) { result ->
                                    result.onSuccess { navController.popBackStack() }
                                    result.onFailure { /* TODO: Show error message to user */ }
                                }
                            },
                            onSaveSleep = { sleep ->
                                activityViewModel.addSleep(sleep) { result ->
                                    result.onSuccess { navController.popBackStack() }
                                    result.onFailure { /* TODO: Show error message to user */ }
                                }
                            },
                            onSaveDiaper = { diaper ->
                                activityViewModel.addDiaper(diaper) { result ->
                                    result.onSuccess { navController.popBackStack() }
                                    result.onFailure { /* TODO: Show error message to user */ }
                                }
                            },
                            onSaveHealth = { health ->
                                activityViewModel.addHealth(health) { result ->
                                    result.onSuccess { navController.popBackStack() }
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
                            onRefresh = { activityViewModel.refreshAllData() }
                        )
                    }
                }
            }
        }
    }
}
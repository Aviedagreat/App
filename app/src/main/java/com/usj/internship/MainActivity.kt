package com.usj.internship

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.usj.internship.ui.screens.AssignmentsScreen
import com.usj.internship.ui.screens.AttendanceScreen
import com.usj.internship.ui.screens.DashboardScreen
import com.usj.internship.ui.screens.ForgotPasswordScreen
import com.usj.internship.ui.screens.InternshipScreen
import com.usj.internship.ui.screens.LoginScreen
import com.usj.internship.ui.screens.LogbookScreen
import com.usj.internship.ui.screens.ProgressScreen
import com.usj.internship.ui.screens.RegisterScreen
import com.usj.internship.ui.screens.TrainingScreen
import com.usj.internship.ui.screens.WelcomeScreen
import com.usj.internship.ui.theme.USJTheme
import com.usj.internship.viewmodel.AuthViewModel
import com.usj.internship.viewmodel.LogbookViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            USJTheme {

                val authVm: AuthViewModel = viewModel()
                val logbookVm: LogbookViewModel = viewModel()

                // Signed-in (verified) users go straight to the dashboard
                var currentScreen by rememberSaveable {
                    mutableStateOf(
                        if (authVm.isUserLoggedIn()) "dashboard" else "welcome"
                    )
                }

                // After a successful verified login, open the dashboard
                LaunchedEffect(authVm.loginSuccess) {
                    if (authVm.loginSuccess) {
                        authVm.consumeLoginSuccess()
                        currentScreen = "dashboard"
                    }
                }

                // System Back button behavior
                BackHandler(
                    enabled = currentScreen != "welcome" &&
                            currentScreen != "login" &&
                            currentScreen != "dashboard"
                ) {
                    currentScreen = when (currentScreen) {
                        "register", "forgot" -> "login"
                        else -> "dashboard"
                    }
                }

                when (currentScreen) {

                    // ================= WELCOME =================
                    "welcome" -> {
                        WelcomeScreen(
                            onGetStarted = { currentScreen = "login" }
                        )
                    }

                    // ================= LOGIN =================
                    "login" -> {
                        LoginScreen(
                            isLoading = authVm.isLoading,
                            serverError = authVm.errorMessage,
                            onClearError = { authVm.clearError() },
                            onLogin = { email, password ->
                                authVm.login(email, password)
                            },
                            onRegister = {
                                authVm.clearError()
                                currentScreen = "register"
                            },
                            onForgotPassword = {
                                authVm.resetForgotState()
                                currentScreen = "forgot"
                            }
                        )
                    }

                    // ================= FORGOT PASSWORD =================
                    "forgot" -> {
                        ForgotPasswordScreen(
                            isLoading = authVm.isLoading,
                            errorMessage = authVm.errorMessage,
                            emailSent = authVm.resetEmailSent,
                            onSendReset = { email ->
                                authVm.sendPasswordReset(email)
                            },
                            onBackToLogin = {
                                authVm.resetForgotState()
                                currentScreen = "login"
                            },
                            onClearError = { authVm.clearError() }
                        )
                    }

                    // ================= REGISTER =================
                    "register" -> {
                        RegisterScreen(
                            isLoading = authVm.isLoading,
                            errorMessage = authVm.errorMessage,
                            registrationDone = authVm.registrationDone,
                            onRegister = { fullName, email, password ->
                                authVm.register(fullName, email, password)
                            },
                            onBackToLogin = {
                                authVm.resetRegistration()
                                currentScreen = "login"
                            },
                            onClearError = { authVm.clearError() }
                        )
                    }

                    // ================= DASHBOARD =================
                    "dashboard" -> {
                        DashboardScreen(
                            userName = authVm.currentUserName(),
                            onTrainingClick = { currentScreen = "training" },
                            onAssignmentsClick = { currentScreen = "assignments" },
                            onInternshipClick = { currentScreen = "internship" },
                            onProgressClick = { currentScreen = "progress" },
                            onAttendanceClick = { currentScreen = "attendance" },
                            onLogbookClick = { currentScreen = "logbook" },
                            onPlacementClick = { currentScreen = "placement" },
                            onEvaluationClick = { currentScreen = "evaluation" },
                            onDocumentClick = { currentScreen = "documents" },
                            onNotificationClick = { currentScreen = "notifications" },
                            onLogout = {
                                authVm.logout()
                                logbookVm.clear()
                                currentScreen = "login"
                            }
                        )
                    }

                    // ================= LOGBOOK =================
                    "logbook" -> {
                        LogbookScreen(
                            vm = logbookVm,
                            onBackClick = { currentScreen = "dashboard" }
                        )
                    }

                    // ================= ATTENDANCE =================
                    "attendance" -> {
                        AttendanceScreen(
                            onBack = { currentScreen = "dashboard" }
                        )
                    }

                    // ================= TRAINING =================
                    "training" -> {
                        TrainingScreen(
                            onBackClick = { currentScreen = "dashboard" }
                        )
                    }

                    // ================= ASSIGNMENTS =================
                    "assignments" -> {
                        AssignmentsScreen(
                            onBackClick = { currentScreen = "dashboard" }
                        )
                    }

                    // ================= INTERNSHIP =================
                    "internship" -> {
                        InternshipScreen(
                            onBackClick = { currentScreen = "dashboard" }
                        )
                    }

                    // ================= PLACEMENT (temporary) =================
                    "placement" -> {
                        InternshipScreen(
                            onBackClick = { currentScreen = "dashboard" }
                        )
                    }

                    // ================= PROGRESS =================
                    "progress" -> {
                        ProgressScreen(
                            onBackClick = { currentScreen = "dashboard" }
                        )
                    }

                    // ================= EVALUATION (temporary) =================
                    "evaluation" -> {
                        ProgressScreen(
                            onBackClick = { currentScreen = "dashboard" }
                        )
                    }

                    // ================= DOCUMENTS (temporary) =================
                    "documents" -> {
                        AssignmentsScreen(
                            onBackClick = { currentScreen = "dashboard" }
                        )
                    }

                    // ================= NOTIFICATIONS (temporary) =================
                    "notifications" -> {
                        ProgressScreen(
                            onBackClick = { currentScreen = "dashboard" }
                        )
                    }

                    // ================= UNKNOWN SCREEN =================
                    else -> {
                        WelcomeScreen(
                            onGetStarted = { currentScreen = "login" }
                        )
                    }
                }
            }
        }
    }
}
@file:OptIn(ExperimentalMaterial3Api::class)

package com.usj.internship.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DashboardItem(
    val title: String,
    val description: String,
    val icon: String,
    val onClick: () -> Unit
)

@Composable
fun DashboardScreen(
    userName: String = "Student",
    onTrainingClick: () -> Unit = {},
    onAssignmentsClick: () -> Unit = {},
    onInternshipClick: () -> Unit = {},
    onProgressClick: () -> Unit = {},
    onAttendanceClick: () -> Unit = {},
    onLogbookClick: () -> Unit = {},
    onPlacementClick: () -> Unit = {},
    onEvaluationClick: () -> Unit = {},
    onDocumentClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {

    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("LOG OUT")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }

    val dashboardItems = listOf(
        DashboardItem(
            title = "Training Modules",
            description = "Access and complete training tasks",
            icon = "📚",
            onClick = onTrainingClick
        ),
        DashboardItem(
            title = "Assignments",
            description = "View and submit internship tasks",
            icon = "📋",
            onClick = onAssignmentsClick
        ),
        DashboardItem(
            title = "Internship Details",
            description = "View internship and supervisor info",
            icon = "🏢",
            onClick = onInternshipClick
        ),
        DashboardItem(
            title = "Placement",
            description = "View your placement information",
            icon = "📍",
            onClick = onPlacementClick
        ),
        DashboardItem(
            title = "Attendance",
            description = "Record and view your attendance",
            icon = "🗓️",
            onClick = onAttendanceClick
        ),
        DashboardItem(
            title = "Logbook",
            description = "Write your daily internship logs",
            icon = "📓",
            onClick = onLogbookClick
        ),
        DashboardItem(
            title = "Progress Tracking",
            description = "Track your completion progress",
            icon = "📊",
            onClick = onProgressClick
        ),
        DashboardItem(
            title = "Evaluation",
            description = "View supervisor evaluations",
            icon = "⭐",
            onClick = onEvaluationClick
        ),
        DashboardItem(
            title = "Documents",
            description = "View internship documents",
            icon = "📄",
            onClick = onDocumentClick
        ),
        DashboardItem(
            title = "Notifications",
            description = "View system notifications",
            icon = "🔔",
            onClick = onNotificationClick
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "USJ-ITMS",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Log out"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome back, $userName 👋",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Internship Training Management System",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProgressCard(
                progress = 0.35f,
                progressPercentageText = "35% Complete",
                daysCompletedText = "18 / 50 Days"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Quick Access",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dashboardItems) { item ->
                    DashboardCard(item = item)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ProgressCard(
    progress: Float,
    progressPercentageText: String,
    daysCompletedText: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Internship Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = progressPercentageText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = daysCompletedText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Keep going! Complete your internship activities.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun DashboardCard(
    item: DashboardItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = item.onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = item.icon,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
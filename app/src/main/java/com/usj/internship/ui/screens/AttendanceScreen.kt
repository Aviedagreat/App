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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AttendanceRecord(
    val date: String,
    val checkIn: String,
    val checkOut: String,
    val status: String
)

@Composable
fun AttendanceScreen(
    onBack: () -> Unit
) {

    val currentDate = SimpleDateFormat(
        "dd MMM yyyy",
        Locale.getDefault()
    ).format(Date())

    val currentTime = {
        SimpleDateFormat(
            "hh:mm a",
            Locale.getDefault()
        ).format(Date())
    }

    var checkInTime = remember {
        mutableStateOf("")
    }

    var checkOutTime = remember {
        mutableStateOf("")
    }

    var status = remember {
        mutableStateOf("Not Marked")
    }

    val attendanceHistory = remember {
        mutableStateListOf(
            AttendanceRecord(
                date = "29 Sep 2026",
                checkIn = "08:05 AM",
                checkOut = "05:00 PM",
                status = "Present"
            ),
            AttendanceRecord(
                date = "28 Sep 2026",
                checkIn = "08:10 AM",
                checkOut = "05:05 PM",
                status = "Present"
            ),
            AttendanceRecord(
                date = "27 Sep 2026",
                checkIn = "08:00 AM",
                checkOut = "04:55 PM",
                status = "Present"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Attendance",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {

                Text(
                    text = "Today's Attendance",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = currentDate,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.primaryContainer
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "Attendance Status",
                            style =
                                MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = status.value,
                            style =
                                MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = "Check In: ${
                                if (checkInTime.value.isEmpty())
                                    "--"
                                else
                                    checkInTime.value
                            }"
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "Check Out: ${
                                if (checkOutTime.value.isEmpty())
                                    "--"
                                else
                                    checkOutTime.value
                            }"
                        )
                    }
                }
            }

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    Button(
                        onClick = {

                            checkInTime.value = currentTime()
                            status.value = "Present"

                        },
                        modifier = Modifier.weight(1f),
                        enabled = checkInTime.value.isEmpty()
                    ) {
                        Text("CHECK IN")
                    }

                    OutlinedButton(
                        onClick = {

                            checkOutTime.value = currentTime()

                        },
                        modifier = Modifier.weight(1f),
                        enabled =
                            checkInTime.value.isNotEmpty() &&
                                    checkOutTime.value.isEmpty()
                    ) {
                        Text("CHECK OUT")
                    }
                }
            }

            item {

                Text(
                    text = "Attendance History",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(attendanceHistory) { record ->

                AttendanceHistoryCard(
                    record = record
                )
            }

            item {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("← BACK TO DASHBOARD")
                }
            }
        }
    }
}

@Composable
private fun AttendanceHistoryCard(
    record: AttendanceRecord
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text = record.date,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = record.status,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Check In: ${record.checkIn}"
            )

            Text(
                text = "Check Out: ${record.checkOut}"
            )
        }
    }
}
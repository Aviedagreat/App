@file:OptIn(ExperimentalMaterial3Api::class)

package com.usj.internship.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usj.internship.viewmodel.LogbookEntry
import com.usj.internship.viewmodel.LogbookViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogbookScreen(
    vm: LogbookViewModel,
    onBackClick: () -> Unit
) {
    var showForm by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.loadEntries()
    }

    // After a successful save, return to the list
    LaunchedEffect(vm.saved) {
        if (vm.saved) {
            vm.consumeSaved()
            showForm = false
        }
    }

    BackHandler(enabled = showForm) {
        showForm = false
        vm.clearError()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (showForm) "New Entry" else "Logbook",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (showForm) {
                                showForm = false
                                vm.clearError()
                            } else {
                                onBackClick()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (!showForm) {
                ExtendedFloatingActionButton(
                    onClick = { showForm = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("NEW ENTRY") }
                )
            }
        }
    ) { paddingValues ->

        if (showForm) {
            LogbookForm(vm = vm, paddingValues = paddingValues)
        } else {
            LogbookList(vm = vm, paddingValues = paddingValues)
        }
    }
}

@Composable
private fun LogbookList(
    vm: LogbookViewModel,
    paddingValues: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        when {
            vm.isLoading && vm.entries.isEmpty() -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            vm.errorMessage != null && vm.entries.isEmpty() -> {
                Text(
                    text = vm.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                )
            }

            vm.entries.isEmpty() -> {
                Text(
                    text = "No entries yet.\nTap NEW ENTRY to write your first log.",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                )
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(vm.entries, key = { it.id }) { entry ->
                        LogbookEntryCard(entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun LogbookEntryCard(entry: LogbookEntry) {

    val statusColor = when (entry.status) {
        "approved" -> Color(0xFF2E7D32)
        "rejected" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = entry.date,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = entry.status.uppercase(),
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = entry.activities,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hours worked: ${entry.hoursWorked}",
                style = MaterialTheme.typography.bodySmall
            )

            if (entry.supervisorComment.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Supervisor: ${entry.supervisorComment}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun LogbookForm(
    vm: LogbookViewModel,
    paddingValues: PaddingValues
) {
    var date by rememberSaveable {
        mutableStateOf(
            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        )
    }
    var title by rememberSaveable { mutableStateOf("") }
    var activities by rememberSaveable { mutableStateOf("") }
    var hoursText by rememberSaveable { mutableStateOf("") }

    val dateValid = Regex("""\d{4}-\d{2}-\d{2}""").matches(date.trim())
    val titleValid = title.trim().length in 3..100
    val activitiesValid = activities.trim().length in 10..2000
    val hours = hoursText.toDoubleOrNull()
    val hoursValid = hours != null && hours > 0 && hours <= 24
    val formValid = dateValid && titleValid && activitiesValid && hoursValid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = date,
            onValueChange = { if (it.length <= 10) date = it; vm.clearError() },
            label = { Text("Date (YYYY-MM-DD)") },
            singleLine = true,
            isError = date.isNotEmpty() && !dateValid,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { if (it.length <= 100) title = it; vm.clearError() },
            label = { Text("Title") },
            singleLine = true,
            supportingText = { Text("3 to 100 characters") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = activities,
            onValueChange = { if (it.length <= 2000) activities = it; vm.clearError() },
            label = { Text("What did you do today?") },
            minLines = 5,
            supportingText = { Text("${activities.length} / 2000 (at least 10)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = hoursText,
            onValueChange = { if (it.length <= 5) hoursText = it; vm.clearError() },
            label = { Text("Hours worked") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = hoursText.isNotEmpty() && !hoursValid,
            supportingText = { Text("Between 0.5 and 24") },
            modifier = Modifier.fillMaxWidth()
        )

        if (vm.errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = vm.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (hours != null) {
                    vm.addEntry(date, title, activities, hours)
                }
            },
            enabled = formValid && !vm.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (vm.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("SUBMIT ENTRY")
            }
        }
    }
}
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class TrainingModule(
    val id: Int,
    val title: String,
    val description: String,
    var isCompleted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScreen(
    onBackClick: () -> Unit
) {
    val modules = remember {
        mutableStateListOf(
            TrainingModule(
                id = 1,
                title = "1. Workplace Orientation",
                description = "Learn about USJ internship guidelines and workplace policies.",
                isCompleted = true
            ),
            TrainingModule(
                id = 2,
                title = "2. Professional Ethics & Safety",
                description = "Understand workplace safety, ethics, and code of conduct.",
                isCompleted = true
            ),
            TrainingModule(
                id = 3,
                title = "3. Technical Tools & Workflows",
                description = "Master the software and tools required for your internship role.",
                isCompleted = false
            ),
            TrainingModule(
                id = 4,
                title = "4. Project Management Basics",
                description = "Learn task management, reporting, and team collaboration.",
                isCompleted = false
            ),
            TrainingModule(
                id = 5,
                title = "5. Final Presentation & Report",
                description = "Prepare your final internship report and presentation slides.",
                isCompleted = false
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Training Modules", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Complete your assigned training modules:",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(modules, key = { it.id }) { module ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (module.isCompleted) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = module.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = module.description,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Checkbox(
                                checked = module.isCompleted,
                                onCheckedChange = { isChecked ->
                                    val index = modules.indexOfFirst { it.id == module.id }
                                    if (index != -1) {
                                        modules[index] = modules[index].copy(isCompleted = isChecked)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

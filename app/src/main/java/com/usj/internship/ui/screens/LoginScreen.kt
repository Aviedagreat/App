package com.usj.internship.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen
            (
    onLogin: (email: String, password: String) -> Unit,
    onRegister: () -> Unit,
    onForgotPassword: () -> Unit = {},
    isLoading: Boolean = false,
    serverError: String? = null,
    onClearError: () -> Unit = {}
)
{

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var showPassword by remember {
        mutableStateOf(false)
    }

    var rememberMe by remember {
        mutableStateOf(false)
    }


    var errorMessage by remember {
        mutableStateOf("")
    }

    var loginAttempts by remember {
        mutableStateOf(0)
    }

    val emailValid =
        email.isNotBlank() &&
                android.util.Patterns.EMAIL_ADDRESS
                    .matcher(email.trim())
                    .matches()

    val passwordValid = password.length >= 8

    val formValid = emailValid && passwordValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "USJ-ITMS",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "UNIVERSITY OF SAINT JOSEPH",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Internship Training Management System",
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Welcome Back Avie",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Sign in securely to continue."
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            // EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = ""
                    onClearError()
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = {
                    Text("Email Address")
                },
                placeholder = {
                    Text("student@example.com")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email"
                    )
                },
                isError = email.isNotEmpty() && !emailValid
            )

            if (email.isNotEmpty() && !emailValid) {
                Text(
                    text = "Please enter a valid email address.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // PASSWORD
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = ""
                    onClearError()
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = {
                    Text("Password")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password"
                    )
                },
                trailingIcon = {

                    IconButton(
                        onClick = {
                            showPassword = !showPassword
                        }
                    ) {

                        Icon(
                            imageVector =
                                if (showPassword) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                            contentDescription =
                                if (showPassword) {
                                    "Hide password"
                                } else {
                                    "Show password"
                                }
                        )
                    }
                },
                visualTransformation =
                    if (showPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                isError = password.isNotEmpty() && !passwordValid
            )

            if (password.isNotEmpty() && !passwordValid) {

                Text(
                    text = "Password must contain at least 8 characters.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // REMEMBER ME + FORGOT PASSWORD
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = {
                        rememberMe = it
                    }
                )

                Text(
                    text = "Remember me"
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                TextButton(
                    onClick = onForgotPassword
                ) {
                    Text("Forgot password?")
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // SECURITY ERROR
            val shownError = serverError ?: errorMessage

            if (shownError.isNotEmpty()) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {

                    Text(
                        text = shownError,
                        color =
                            MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(14.dp),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            // LOGIN BUTTON
            Button(
                onClick = {

                    if (!formValid) {

                        errorMessage =
                            "Please enter a valid email address and password."

                    } else {

                        /*
                         * IMPORTANT:
                         * Do NOT navigate to the dashboard here.
                         *
                         * The authentication layer will decide whether
                         * login succeeds or fails.
                         */



                        onLogin(
                            email.trim(),
                            password
                        )
                    }
                },
                enabled = formValid && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {

                if (isLoading) {

                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp
                    )

                } else {

                    Text(
                        text = "SIGN IN",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // REGISTER
            OutlinedButton(
                onClick = onRegister,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("CREATE NEW ACCOUNT")
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // SECURITY INFORMATION
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Secure login",
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.height(0.dp)
                )

                Text(
                    text = " Secure authentication",
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Never share your password with another person.",
                fontSize = 12.sp
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "USJ-ITMS",
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "University Internship Management System",
                fontSize = 11.sp
            )
        }
    }
}
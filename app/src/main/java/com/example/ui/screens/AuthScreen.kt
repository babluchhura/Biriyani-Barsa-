package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.MainViewModel
import com.example.Screen
import com.example.ui.theme.*

enum class AuthTab {
    MOBILE_OTP,
    EMAIL_PASSWORD,
    REGISTER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    var selectedTab by remember { mutableStateOf(AuthTab.MOBILE_OTP) }
    var phoneNumber by remember { mutableStateOf("9668612323") }
    var otpCode by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("yogeshchhura@gmail.com") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }

    var showForgotDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.testTag("auth_screen"),
        topBar = {
            TopAppBar(
                title = { Text("Customer Account", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("auth_back_btn")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Royal Logo Header
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Biriyani Barsa",
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "BIRIYANI BARSA",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = GoldLight,
                    letterSpacing = 1.sp
                )
            )
            Text(
                text = "Sign in to unlock exclusive royal rewards & express delivery",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = GoldPrimary
            ) {
                Tab(
                    selected = selectedTab == AuthTab.MOBILE_OTP,
                    onClick = { selectedTab = AuthTab.MOBILE_OTP },
                    text = { Text("Mobile OTP", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == AuthTab.EMAIL_PASSWORD,
                    onClick = { selectedTab = AuthTab.EMAIL_PASSWORD },
                    text = { Text("Email Login", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == AuthTab.REGISTER,
                    onClick = { selectedTab = AuthTab.REGISTER },
                    text = { Text("Register", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (selectedTab) {
                AuthTab.MOBILE_OTP -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Mobile Number") },
                            leadingIcon = { Text("+91 ", fontWeight = FontWeight.Bold, color = GoldPrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_phone_input")
                        )

                        if (!isOtpSent) {
                            Button(
                                onClick = {
                                    if (phoneNumber.length >= 10) {
                                        isOtpSent = true
                                        otpCode = "123456" // Auto-fill test code for convenience
                                        viewModel.emitMessage("OTP sent to +91 $phoneNumber. Test code: 123456")
                                    } else {
                                        viewModel.emitMessage("Please enter a valid 10-digit mobile number")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary,
                                    contentColor = Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("send_otp_btn")
                            ) {
                                Text("Send Verification OTP", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            OutlinedTextField(
                                value = otpCode,
                                onValueChange = { otpCode = it },
                                label = { Text("6-Digit OTP Code") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = GoldPrimary
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_otp_input")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { otpCode = "123456" }) {
                                    Text("Auto-fill Test OTP (123456)", color = GoldPrimary, fontSize = 12.sp)
                                }
                                TextButton(onClick = { viewModel.emitMessage("New OTP sent!") }) {
                                    Text("Resend Code", fontSize = 12.sp)
                                }
                            }

                            Button(
                                onClick = {
                                    if (otpCode == "123456" || otpCode.length == 6) {
                                        viewModel.loginCustomer(
                                            phoneOrEmail = "+91 $phoneNumber",
                                            name = "Yogesh Chhura"
                                        )
                                    } else {
                                        viewModel.emitMessage("Invalid OTP code. Use test code 123456")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary,
                                    contentColor = Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("verify_otp_btn")
                            ) {
                                Text("Verify & Enter", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                AuthTab.EMAIL_PASSWORD -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = GoldPrimary
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_input")
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = GoldPrimary
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showForgotDialog = true }) {
                                Text("Forgot Password?", color = GoldPrimary, fontSize = 12.sp)
                            }
                        }

                        Button(
                            onClick = {
                                if (email.isNotBlank()) {
                                    viewModel.loginCustomer(email, email.substringBefore("@"))
                                } else {
                                    viewModel.emitMessage("Please enter your email")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("login_submit_btn")
                        ) {
                            Text("Sign In", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                AuthTab.REGISTER -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = regName,
                            onValueChange = { regName = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = GoldPrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("Email Address") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = GoldPrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = regPhone,
                            onValueChange = { regPhone = it },
                            label = { Text("Mobile Number") },
                            leadingIcon = { Text("+91 ", fontWeight = FontWeight.Bold, color = GoldPrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("Create Password") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = GoldPrimary) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (regName.isNotBlank() && regPhone.isNotBlank()) {
                                    viewModel.loginCustomer(regPhone, regName)
                                } else {
                                    viewModel.emitMessage("Please complete all registration fields")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Create Royal Account", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showForgotDialog) {
        var resetEmail by remember { mutableStateOf(email) }
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = { Text("Forgot Password", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter your registered email address to receive password reset instructions.")
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.emitMessage("Password reset instructions sent to $resetEmail")
                        showForgotDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Black)
                ) {
                    Text("Send Reset Link", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

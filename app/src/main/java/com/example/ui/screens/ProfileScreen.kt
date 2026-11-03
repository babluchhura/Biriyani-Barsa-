package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.Screen
import com.example.data.repository.RestaurantRepository
import com.example.ui.components.ContactActionBar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.Home)
    }

    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    val savedAddresses by viewModel.savedAddresses.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.testTag("profile_screen"),
        topBar = {
            TopAppBar(
                title = { Text("Customer Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.Home) },
                        modifier = Modifier.testTag("profile_back_btn")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.AdminLogin) },
                        modifier = Modifier.testTag("admin_lock_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Login",
                            tint = GoldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Info Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GoldContainer,
                            border = BorderStroke(2.dp, GoldPrimary),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier
                                    .padding(18.dp)
                                    .size(44.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = user.phone,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = GoldPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = { showEditProfileDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("edit_profile_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile Details", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Quick Menu Items
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column {
                        ProfileMenuRow(
                            icon = Icons.Default.ReceiptLong,
                            title = "My Orders",
                            subtitle = "View order status & order history",
                            onClick = { viewModel.navigateTo(Screen.OrdersHistory) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                        ProfileMenuRow(
                            icon = Icons.Default.Favorite,
                            title = "Favorites & Wishlist",
                            subtitle = "${wishlistIds.size} saved royal dishes",
                            onClick = { viewModel.navigateTo(Screen.Menu()) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                        ProfileMenuRow(
                            icon = Icons.Default.ConfirmationNumber,
                            title = "Offers & Coupons",
                            subtitle = "Exclusive biryani discounts",
                            onClick = { viewModel.navigateTo(Screen.Cart) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                        ProfileMenuRow(
                            icon = Icons.Default.RateReview,
                            title = "Send Feedback & Review",
                            subtitle = "Share your dining experience with chef",
                            onClick = { showFeedbackDialog = true }
                        )
                    }
                }
            }

            // Saved Addresses Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Saved Delivery Addresses",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = GoldPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        savedAddresses.forEach { addr ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (addr.label == "Home") Icons.Default.Home else Icons.Default.Work,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = addr.label,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = addr.fullFormattedAddress,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Contact & Help section
            item {
                ContactActionBar(
                    onWhatsAppClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://wa.me/919668612323")
                        )
                        context.startActivity(intent)
                    },
                    onCallClick = {
                        val intent = Intent(
                            Intent.ACTION_DIAL,
                            Uri.parse("tel:+9668612323")
                        )
                        context.startActivity(intent)
                    }
                )
            }

            // Private Admin Portal Entry Notice
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.clickable { viewModel.navigateTo(Screen.AdminLogin) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GoldContainer,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Restaurant Admin Portal",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "Restricted private access for management only",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = GoldPrimary
                        )
                    }
                }
            }

            // Logout / Sign in button
            item {
                OutlinedButton(
                    onClick = { viewModel.navigateTo(Screen.Auth) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = NonVegRed
                    ),
                    border = BorderStroke(1.dp, NonVegRed.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Switch / Sign Out Customer Account", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(user.name) }
        var editEmail by remember { mutableStateOf(user.email) }
        var editPhone by remember { mutableStateOf(user.phone) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile Details", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Mobile Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(editName, editEmail, editPhone)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Black)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Feedback Dialog
    if (showFeedbackDialog) {
        var feedbackRating by remember { mutableStateOf(5) }
        var feedbackComment by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showFeedbackDialog = false },
            title = { Text("Customer Feedback", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Rate your Biriyani Barsa experience:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { feedbackRating = star }) {
                                Icon(
                                    imageVector = if (star <= feedbackRating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "$star Stars",
                                    tint = StarGold,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = feedbackComment,
                        onValueChange = { feedbackComment = it },
                        placeholder = { Text("Tell us about food taste, delivery, or packaging...") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.emitMessage("Thank you for your valuable feedback!")
                        showFeedbackDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Black)
                ) {
                    Text("Submit Feedback", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFeedbackDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileMenuRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier
                    .padding(10.dp)
                    .size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

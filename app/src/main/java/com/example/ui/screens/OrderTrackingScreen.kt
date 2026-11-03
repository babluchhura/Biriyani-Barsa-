package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.data.model.OrderStatus
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    orderId: String,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.Home)
    }

    val context = LocalContext.current
    val orders by viewModel.orders.collectAsState()
    val order = orders.find { it.id == orderId } ?: orders.firstOrNull()

    val statuses = listOf(
        OrderStatus.ORDER_PLACED,
        OrderStatus.ORDER_ACCEPTED,
        OrderStatus.FOOD_PREPARING,
        OrderStatus.READY_FOR_DELIVERY,
        OrderStatus.OUT_FOR_DELIVERY,
        OrderStatus.DELIVERED
    )

    Scaffold(
        modifier = modifier.testTag("order_tracking_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Track Order #${order?.id ?: orderId}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Live updates from Biriyani Barsa kitchen",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.Home) },
                        modifier = Modifier.testTag("tracking_back_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://wa.me/919668612323")
                            )
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "WhatsApp Help",
                            tint = WhatsAppGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (order == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Order not found", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Estimated Arrival Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (order.status == OrderStatus.DELIVERED) VegGreen.copy(alpha = 0.15f) else GoldContainer
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (order.status == OrderStatus.DELIVERED) VegGreen else GoldPrimary
                        )
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (order.status == OrderStatus.DELIVERED) "ORDER DELIVERED" else "ESTIMATED ARRIVAL",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (order.status == OrderStatus.DELIVERED) VegGreen else GoldPrimary
                                        )
                                    )
                                    Text(
                                        text = if (order.status == OrderStatus.DELIVERED) "Delivered Hot & Fresh" else "25 - 35 mins",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (order.status == OrderStatus.DELIVERED) VegGreen else GoldLight
                                        )
                                    )
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = if (order.status == OrderStatus.DELIVERED) VegGreen else GoldPrimary,
                                    modifier = Modifier.size(46.dp)
                                ) {
                                    Icon(
                                        imageVector = if (order.status == OrderStatus.DELIVERED) Icons.Default.Check else Icons.Default.Moped,
                                        contentDescription = null,
                                        tint = Black,
                                        modifier = Modifier
                                            .padding(11.dp)
                                            .size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Current Status: ${order.status.title}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = order.status.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )

                            // Interactive Simulation trigger
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surface
                                ) {
                                    Text(
                                        text = "LIVE STATUS DEMO",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                TextButton(
                                    onClick = { viewModel.advanceOrderStatus(order.id) },
                                    modifier = Modifier.testTag("advance_status_demo_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FastForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = GoldPrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Next Status Step",
                                        fontWeight = FontWeight.Bold,
                                        color = GoldPrimary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 7-Stage Stepper Tracker Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Live Order Progress",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            val currentStepIndex = order.status.stepIndex

                            statuses.forEachIndexed { index, status ->
                                val isDone = currentStepIndex > index
                                val isCurrent = currentStepIndex == index
                                val isPending = currentStepIndex < index

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Indicator dot & line column
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(32.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = when {
                                                isDone -> VegGreen
                                                isCurrent -> GoldPrimary
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            },
                                            border = BorderStroke(
                                                2.dp,
                                                when {
                                                    isDone -> VegGreen
                                                    isCurrent -> GoldLight
                                                    else -> MaterialTheme.colorScheme.outline
                                                }
                                            ),
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            if (isDone) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier
                                                        .padding(4.dp)
                                                        .size(14.dp)
                                                )
                                            } else if (isCurrent) {
                                                Box(
                                                    modifier = Modifier
                                                        .padding(6.dp)
                                                        .clip(CircleShape)
                                                        .background(Black)
                                                )
                                            }
                                        }

                                        if (index < statuses.size - 1) {
                                            Box(
                                                modifier = Modifier
                                                    .width(2.dp)
                                                    .height(38.dp)
                                                    .background(
                                                        if (isDone) VegGreen else MaterialTheme.colorScheme.outline
                                                    )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.padding(bottom = if (index < statuses.size - 1) 16.dp else 0.dp)) {
                                        Text(
                                            text = status.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Medium,
                                                color = when {
                                                    isCurrent -> GoldPrimary
                                                    isDone -> MaterialTheme.colorScheme.onSurface
                                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                                }
                                            )
                                        )
                                        Text(
                                            text = status.description,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Delivery Partner Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = GoldContainer,
                                modifier = Modifier.size(46.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SportsMotorsports,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(24.dp)
                                    )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = order.driverName,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "Royal Delivery Partner • Vaccinated",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                            IconButton(
                                onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_DIAL,
                                        Uri.parse("tel:${order.driverPhone}")
                                    )
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .background(GoldPrimary, CircleShape)
                                    .size(40.dp)
                                    .testTag("call_rider_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Call Rider",
                                    tint = Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Order Items Summary
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Order Items (${order.items.size})",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            order.items.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${item.quantity}x ${item.foodItem.name}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "₹${item.itemTotal.toInt()}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GoldPrimary
                                        )
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = MaterialTheme.colorScheme.outline
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total Paid (${order.paymentMethod.title})",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "₹${order.grandTotal.toInt()}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GoldPrimary
                                    )
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

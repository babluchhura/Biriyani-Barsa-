package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.MainViewModel
import com.example.Screen
import com.example.ui.components.VegNonVegBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val cartItems by viewModel.cartItems.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val coupons by viewModel.coupons.collectAsState()

    var couponInput by remember { mutableStateOf("") }
    var cookingNotes by remember { mutableStateOf("") }

    val subtotal = remember(cartItems) {
        cartItems.sumOf { it.itemTotal }
    }

    val discount = remember(appliedCoupon, subtotal) {
        if (appliedCoupon != null && subtotal >= appliedCoupon!!.minOrderAmount) {
            val calc = (subtotal * appliedCoupon!!.discountPercent) / 100.0
            calc.coerceAtMost(appliedCoupon!!.maxDiscount)
        } else 0.0
    }

    val deliveryFee = remember(subtotal, appliedCoupon) {
        if (subtotal > 499.0 || (appliedCoupon?.code == "FREESHIP")) 0.0 else 40.0
    }

    val taxGst = remember(subtotal, discount) {
        (subtotal - discount).coerceAtLeast(0.0) * 0.05
    }

    val packagingFee = 20.0

    val grandTotal = remember(subtotal, discount, deliveryFee, taxGst, packagingFee) {
        (subtotal - discount + deliveryFee + taxGst + packagingFee).coerceAtLeast(0.0)
    }

    Scaffold(
        modifier = modifier.testTag("cart_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Royal Cart (${cartItems.sumOf { it.quantity }})",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("cart_back_btn")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        TextButton(
                            onClick = { viewModel.clearCart() },
                            modifier = Modifier.testTag("clear_cart_btn")
                        ) {
                            Text(
                                text = "Clear",
                                color = NonVegRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "GRAND TOTAL",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    text = "₹${grandTotal.toInt()}",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = GoldPrimary
                                    )
                                )
                            }

                            Button(
                                onClick = { viewModel.navigateTo(Screen.Checkout) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary,
                                    contentColor = Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                                modifier = Modifier.testTag("proceed_checkout_btn")
                            ) {
                                Text(
                                    text = "Proceed to Checkout",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = GoldContainer,
                        modifier = Modifier.size(96.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier
                                .padding(24.dp)
                                .size(48.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Your royal plate is empty!",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Explore the authentic flavours of dum biryani and delicious starters now.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.navigateTo(Screen.Menu()) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Browse Royal Menu",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Cart Items
                items(cartItems) { item ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.testTag("cart_item_${item.foodItem.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = item.foodItem.imageUrl,
                                contentDescription = item.foodItem.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    VegNonVegBadge(isVeg = item.foodItem.isVeg)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = item.foodItem.name,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        maxLines = 1
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "₹${item.foodItem.finalPrice.toInt()} each",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "₹${item.itemTotal.toInt()}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GoldPrimary
                                    )
                                )
                            }

                            // Stepper & Delete
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.deleteFromCart(item.foodItem) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Remove",
                                        tint = NonVegRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(GoldContainer, RoundedCornerShape(8.dp))
                                        .border(BorderStroke(1.dp, GoldPrimary), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 2.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.removeFromCart(item.foodItem) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = "Minus",
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Text(
                                        text = "${item.quantity}",
                                        fontWeight = FontWeight.Bold,
                                        color = OnGoldContainer,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )
                                    IconButton(
                                        onClick = { viewModel.addToCart(item.foodItem) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Plus",
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Coupon Code Section
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ConfirmationNumber,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Apply Royal Coupon",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = couponInput,
                                    onValueChange = { couponInput = it.uppercase() },
                                    placeholder = { Text("Enter coupon code (e.g. BARSA50)", fontSize = 12.sp) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("coupon_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (couponInput.isNotBlank()) {
                                            viewModel.applyCoupon(couponInput)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GoldPrimary,
                                        contentColor = Black
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("apply_coupon_btn")
                                ) {
                                    Text("Apply", fontWeight = FontWeight.Bold)
                                }
                            }

                            // Available coupon quick chips
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(coupons) { c ->
                                    AssistChip(
                                        onClick = {
                                            couponInput = c.code
                                            viewModel.applyCoupon(c.code)
                                        },
                                        label = { Text(c.code, fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.LocalOffer,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = GoldPrimary
                                            )
                                        }
                                    )
                                }
                            }

                            // Applied Coupon Status
                            if (appliedCoupon != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GoldContainer,
                                    border = BorderStroke(1.dp, GoldPrimary)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "🎉 Coupon '${appliedCoupon!!.code}' Applied!",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = GoldLight
                                                )
                                            )
                                            Text(
                                                text = "You saved ₹${discount.toInt()} on this order",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = OnGoldContainer
                                                )
                                            )
                                        }
                                        TextButton(onClick = { viewModel.removeCoupon() }) {
                                            Text("Remove", color = NonVegRed, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Special Cooking Instructions
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DinnerDining,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Cooking Instructions for Chef",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = cookingNotes,
                                onValueChange = { cookingNotes = it },
                                placeholder = { Text("e.g. Less spicy, extra onions, disposable cutlery please", fontSize = 12.sp) },
                                maxLines = 2,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("cooking_instructions_input")
                            )
                        }
                    }
                }

                // Detailed Bill Summary
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Bill Details",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            BillRow(label = "Item Subtotal", value = "₹${subtotal.toInt()}")

                            if (discount > 0) {
                                BillRow(
                                    label = "Coupon Discount (${appliedCoupon?.code})",
                                    value = "-₹${discount.toInt()}",
                                    valueColor = VegGreen
                                )
                            }

                            BillRow(
                                label = "Delivery Charges",
                                value = if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}",
                                valueColor = if (deliveryFee == 0.0) VegGreen else MaterialTheme.colorScheme.onSurface
                            )

                            BillRow(label = "GST & Restaurant Taxes (5%)", value = "₹${taxGst.toInt()}")
                            BillRow(label = "Royal Handi Packaging Fee", value = "₹${packagingFee.toInt()}")

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = MaterialTheme.colorScheme.outline
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "To Pay",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "₹${grandTotal.toInt()}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = GoldPrimary
                                    )
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun BillRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        )
    }
}

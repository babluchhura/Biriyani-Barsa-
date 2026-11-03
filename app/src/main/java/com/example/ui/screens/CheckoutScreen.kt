package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.data.model.DeliveryAddress
import com.example.data.model.PaymentMethod
import com.example.ui.theme.*
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val cartItems by viewModel.cartItems.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val savedAddresses by viewModel.savedAddresses.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.UPI_PAYMENT) }
    var selectedUpiApp by remember { mutableStateOf("Google Pay") }
    var upiIdInput by remember { mutableStateOf("9668612323@okaxis") }
    var cardNumber by remember { mutableStateOf("4532 •••• •••• 8821") }
    var cardExpiry by remember { mutableStateOf("11/28") }
    var cardCvv by remember { mutableStateOf("•••") }
    var cookingNotes by remember { mutableStateOf("") }
    var showAddAddressDialog by remember { mutableStateOf(false) }

    val subtotal = remember(cartItems) { cartItems.sumOf { it.itemTotal } }
    val discount = remember(appliedCoupon, subtotal) {
        if (appliedCoupon != null && subtotal >= appliedCoupon!!.minOrderAmount) {
            val calc = (subtotal * appliedCoupon!!.discountPercent) / 100.0
            calc.coerceAtMost(appliedCoupon!!.maxDiscount)
        } else 0.0
    }
    val deliveryFee = remember(subtotal, appliedCoupon) {
        if (subtotal > 499.0 || (appliedCoupon?.code == "FREESHIP")) 0.0 else 40.0
    }
    val taxGst = remember(subtotal, discount) { (subtotal - discount).coerceAtLeast(0.0) * 0.05 }
    val packagingFee = 20.0
    val grandTotal = remember(subtotal, discount, deliveryFee, taxGst, packagingFee) {
        (subtotal - discount + deliveryFee + taxGst + packagingFee).coerceAtLeast(0.0)
    }

    Scaffold(
        modifier = modifier.testTag("checkout_screen"),
        topBar = {
            TopAppBar(
                title = { Text("Checkout & Payment", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("checkout_back_btn")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL PAYABLE",
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
                        onClick = {
                            viewModel.placeOrder(
                                paymentMethod = selectedPaymentMethod,
                                cookingNotes = cookingNotes
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                        modifier = Modifier.testTag("place_order_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Place Order",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Customer Info card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
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
                                text = "Contact Information",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${currentUser.name} • ${currentUser.phone}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = currentUser.email,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            // Delivery Address Selector
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
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
                                text = "Delivery Address",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            TextButton(
                                onClick = { showAddAddressDialog = true },
                                modifier = Modifier.testTag("add_new_address_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Add New",
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        savedAddresses.forEach { addr ->
                            val isSelected = addr.id == selectedAddress.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) GoldContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) GoldPrimary else Color.Transparent
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { viewModel.selectAddress(addr) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { viewModel.selectAddress(addr) },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = GoldPrimary
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = addr.label,
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) GoldLight else MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Text(
                                            text = addr.fullFormattedAddress,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (isSelected) OnGoldContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Payment Methods
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Payment Method",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // 1. UPI Payment Option
                        PaymentOptionRow(
                            title = "UPI Payment (GPay, PhonePe, Paytm)",
                            subtitle = "Fastest & Recommended (Zero extra fees)",
                            icon = Icons.Default.QrCode,
                            isSelected = selectedPaymentMethod == PaymentMethod.UPI_PAYMENT,
                            onClick = { selectedPaymentMethod = PaymentMethod.UPI_PAYMENT }
                        )

                        AnimatedVisibility(visible = selectedPaymentMethod == PaymentMethod.UPI_PAYMENT) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Select UPI App / ID",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("Google Pay", "PhonePe", "Paytm").forEach { app ->
                                        FilterChip(
                                            selected = selectedUpiApp == app,
                                            onClick = { selectedUpiApp = app },
                                            label = { Text(app, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = GoldPrimary,
                                                selectedLabelColor = Black
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = upiIdInput,
                                    onValueChange = { upiIdInput = it },
                                    label = { Text("UPI ID (e.g. mobile@upi)", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // 2. Cash on Delivery
                        PaymentOptionRow(
                            title = "Cash on Delivery (COD)",
                            subtitle = "Pay cash or UPI to rider at doorstep",
                            icon = Icons.Default.Payments,
                            isSelected = selectedPaymentMethod == PaymentMethod.CASH_ON_DELIVERY,
                            onClick = { selectedPaymentMethod = PaymentMethod.CASH_ON_DELIVERY }
                        )

                        // 3. Online Cards / Netbanking
                        PaymentOptionRow(
                            title = "Credit / Debit Cards",
                            subtitle = "Visa, MasterCard, RuPay cards accepted",
                            icon = Icons.Default.CreditCard,
                            isSelected = selectedPaymentMethod == PaymentMethod.ONLINE_CARD,
                            onClick = { selectedPaymentMethod = PaymentMethod.ONLINE_CARD }
                        )

                        AnimatedVisibility(visible = selectedPaymentMethod == PaymentMethod.ONLINE_CARD) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = cardNumber,
                                    onValueChange = { cardNumber = it },
                                    label = { Text("Card Number", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = cardExpiry,
                                        onValueChange = { cardExpiry = it },
                                        label = { Text("Valid Thru (MM/YY)", fontSize = 11.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = cardCvv,
                                        onValueChange = { cardCvv = it },
                                        label = { Text("CVV", fontSize = 11.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Add Address Dialog
    if (showAddAddressDialog) {
        var label by remember { mutableStateOf("Home") }
        var street by remember { mutableStateOf("") }
        var locality by remember { mutableStateOf("") }
        var landmark by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("Bangomunda, Balangir") }
        var pincode by remember { mutableStateOf("767037") }

        AlertDialog(
            onDismissRequest = { showAddAddressDialog = false },
            title = { Text("Add Delivery Address", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Home", "Office", "Other").forEach { l ->
                            FilterChip(
                                selected = label == l,
                                onClick = { label = l },
                                label = { Text(l, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPrimary,
                                    selectedLabelColor = Black
                                )
                            )
                        }
                    }
                    OutlinedTextField(
                        value = street,
                        onValueChange = { street = it },
                        label = { Text("House / Flat / Street Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = locality,
                        onValueChange = { locality = it },
                        label = { Text("Area / Locality") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = landmark,
                        onValueChange = { landmark = it },
                        label = { Text("Nearby Landmark") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (street.isNotBlank() && locality.isNotBlank()) {
                            viewModel.addAddress(
                                DeliveryAddress(
                                    id = "addr_" + UUID.randomUUID().toString().take(6),
                                    label = label,
                                    street = street,
                                    locality = locality,
                                    landmark = landmark,
                                    city = city,
                                    pincode = pincode
                                )
                            )
                            showAddAddressDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Black
                    )
                ) {
                    Text("Save Address", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAddressDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PaymentOptionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) GoldContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, if (isSelected) GoldPrimary else Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) GoldLight else MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isSelected) OnGoldContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
            )
        }
    }
}

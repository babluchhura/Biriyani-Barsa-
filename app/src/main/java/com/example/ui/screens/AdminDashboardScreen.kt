package com.example.ui.screens

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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.MainViewModel
import com.example.Screen
import com.example.data.model.*
import com.example.ui.components.VegNonVegBadge
import com.example.ui.theme.*
import java.util.UUID

enum class AdminTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    OVERVIEW("Overview", Icons.Default.Dashboard),
    FOOD_MENU("Food Catalog", Icons.Default.RestaurantMenu),
    ORDERS("Orders", Icons.Default.ReceiptLong),
    CUSTOMERS("Customers", Icons.Default.People),
    OFFERS("Coupons", Icons.Default.LocalOffer),
    REPORTS("Reports", Icons.Default.BarChart)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.Home)
    }

    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val foodItems by viewModel.foodItems.collectAsState()
    val coupons by viewModel.coupons.collectAsState()
    val restaurantAddress by viewModel.restaurantAddress.collectAsState()

    var selectedTab by remember { mutableStateOf(AdminTab.OVERVIEW) }

    // Dialog states
    var showAddFoodDialog by remember { mutableStateOf(false) }
    var foodToEdit by remember { mutableStateOf<FoodItem?>(null) }
    var showAddCouponDialog by remember { mutableStateOf(false) }
    var showEditAddressDialog by remember { mutableStateOf(false) }

    // Security Gate check
    if (!isAdminLoggedIn) {
        Scaffold(
            modifier = modifier.testTag("access_denied_screen"),
            topBar = {
                TopAppBar(
                    title = { Text("Security Verification") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.navigateTo(Screen.Home) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
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
                        color = NonVegRed.copy(alpha = 0.2f),
                        modifier = Modifier.size(90.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = NonVegRed,
                            modifier = Modifier
                                .padding(24.dp)
                                .size(42.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "ACCESS DENIED",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = NonVegRed,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This area is strictly restricted to Biriyani Barsa authorized restaurant management. Customers cannot access the Admin Dashboard.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.navigateTo(Screen.AdminLogin) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = Black
                        )
                    ) {
                        Text("Log In as Administrator", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(onClick = { viewModel.navigateTo(Screen.Home) }) {
                        Text("Return to Customer App")
                    }
                }
            }
        }
        return
    }

    // Authenticated Admin Dashboard
    val totalRevenue = remember(orders) {
        orders.filter { it.status != OrderStatus.CANCELLED }.sumOf { it.grandTotal }
    }
    val pendingOrdersCount = remember(orders) {
        orders.count { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED }
    }
    val completedOrdersCount = remember(orders) {
        orders.count { it.status == OrderStatus.DELIVERED }
    }
    val cancelledOrdersCount = remember(orders) {
        orders.count { it.status == OrderStatus.CANCELLED }
    }

    Scaffold(
        modifier = modifier.testTag("admin_dashboard_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "BIRIYANI BARSA ADMIN",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = GoldLight
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = VegGreen
                            ) {
                                Text(
                                    text = "LIVE",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Restaurant Management & Operations",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.logoutAdmin() },
                        modifier = Modifier.testTag("admin_logout_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout Admin",
                            tint = NonVegRed
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                AdminTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Black,
                            selectedTextColor = GoldLight,
                            indicatorColor = GoldPrimary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AdminTab.OVERVIEW -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "Restaurant Operations Overview",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                        }

                        // Metric Grid Rows
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AdminMetricCard(
                                    title = "Total Revenue",
                                    value = "₹${totalRevenue.toInt()}",
                                    subtitle = "+18% from last week",
                                    icon = Icons.Default.CurrencyRupee,
                                    color = GoldPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                AdminMetricCard(
                                    title = "Total Orders",
                                    value = "${orders.size}",
                                    subtitle = "$pendingOrdersCount currently active",
                                    icon = Icons.Default.ReceiptLong,
                                    color = StatusBlue,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AdminMetricCard(
                                    title = "Pending Kitchen",
                                    value = "$pendingOrdersCount",
                                    subtitle = "Needs chef attention",
                                    icon = Icons.Default.SoupKitchen,
                                    color = GoldLight,
                                    modifier = Modifier.weight(1f)
                                )
                                AdminMetricCard(
                                    title = "Completed",
                                    value = "$completedOrdersCount",
                                    subtitle = "$cancelledOrdersCount cancelled",
                                    icon = Icons.Default.CheckCircle,
                                    color = VegGreen,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AdminMetricCard(
                                    title = "Total Products",
                                    value = "${foodItems.size}",
                                    subtitle = "Across 12 categories",
                                    icon = Icons.Default.RestaurantMenu,
                                    color = Color(0xFFAB47BC),
                                    modifier = Modifier.weight(1f)
                                )
                                AdminMetricCard(
                                    title = "Active Coupons",
                                    value = "${coupons.count { it.isActive }}",
                                    subtitle = "Special promo codes",
                                    icon = Icons.Default.LocalOffer,
                                    color = Color(0xFF26A69A),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Quick action buttons
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Quick Dispatch & Management",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { selectedTab = AdminTab.ORDERS },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Black)
                                        ) {
                                            Text("Manage Orders", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        OutlinedButton(
                                            onClick = { showAddFoodDialog = true },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("+ Add Dish", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Restaurant Address & Location Management
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = null,
                                                tint = GoldPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Restaurant Location & Address",
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleSmall
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "📍 Restaurant Address:",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GoldLight
                                        )
                                    )
                                    Text(
                                        text = restaurantAddress,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = { showEditAddressDialog = true },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = GoldPrimary,
                                            contentColor = Black
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("admin_change_address_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.EditLocationAlt,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Change Restaurant Address", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                AdminTab.FOOD_MENU -> {
                    var catalogSearch by remember { mutableStateOf("") }
                    val filteredCatalog = remember(foodItems, catalogSearch) {
                        if (catalogSearch.isBlank()) foodItems
                        else foodItems.filter { it.name.contains(catalogSearch, ignoreCase = true) || it.category.displayName.contains(catalogSearch, ignoreCase = true) }
                    }

                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Menu Catalog (${foodItems.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Button(
                                onClick = { showAddFoodDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Black),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("admin_add_dish_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Dish", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        OutlinedTextField(
                            value = catalogSearch,
                            onValueChange = { catalogSearch = it },
                            placeholder = { Text("Filter dishes in catalog...") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredCatalog) { item ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = item.imageUrl,
                                            contentDescription = item.name,
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                VegNonVegBadge(isVeg = item.isVeg)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = item.name,
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    maxLines = 1
                                                )
                                            }
                                            Text(
                                                text = "${item.category.displayName} • ₹${item.finalPrice.toInt()}",
                                                style = MaterialTheme.typography.bodySmall.copy(color = GoldPrimary, fontWeight = FontWeight.Bold)
                                            )
                                        }

                                        // In Stock / Sold Out toggle switch
                                        Column(horizontalAlignment = Alignment.End) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = if (item.isAvailable) "In Stock" else "Sold Out",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (item.isAvailable) VegGreen else NonVegRed
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Switch(
                                                    checked = item.isAvailable,
                                                    onCheckedChange = { viewModel.toggleFoodAvailability(item.id) },
                                                    modifier = Modifier.size(36.dp)
                                                )
                                            }

                                            Row {
                                                IconButton(
                                                    onClick = { foodToEdit = item },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                                }
                                                IconButton(
                                                    onClick = { viewModel.deleteFoodItem(item.id) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NonVegRed, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            item { Spacer(modifier = Modifier.height(20.dp)) }
                        }
                    }
                }

                AdminTab.ORDERS -> {
                    var orderFilter by remember { mutableStateOf("All") }
                    val filteredOrders = remember(orders, orderFilter) {
                        when (orderFilter) {
                            "Pending" -> orders.filter { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED }
                            "Completed" -> orders.filter { it.status == OrderStatus.DELIVERED }
                            "Cancelled" -> orders.filter { it.status == OrderStatus.CANCELLED }
                            else -> orders
                        }
                    }

                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("All", "Pending", "Completed", "Cancelled").forEach { f ->
                                FilterChip(
                                    selected = orderFilter == f,
                                    onClick = { orderFilter = f },
                                    label = { Text(f, fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GoldPrimary,
                                        selectedLabelColor = Black
                                    )
                                )
                            }
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredOrders) { ord ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Order #${ord.id}",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = GoldContainer
                                            ) {
                                                Text(
                                                    text = ord.status.title,
                                                    color = GoldLight,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Customer: ${ord.customerName} • ${ord.customerPhone}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        )
                                        Text(
                                            text = "Deliver To: ${ord.address.street}, ${ord.address.locality}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        )

                                        if (ord.cookingNotes.isNotBlank()) {
                                            Text(
                                                text = "Chef Notes: \"${ord.cookingNotes}\"",
                                                style = MaterialTheme.typography.bodySmall.copy(color = GoldPrimary, fontWeight = FontWeight.Bold)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        ord.items.forEach { itm ->
                                            Text(
                                                text = "• ${itm.quantity}x ${itm.foodItem.name} (₹${itm.itemTotal.toInt()})",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }

                                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline)

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Total: ₹${ord.grandTotal.toInt()} (${ord.paymentMethod.title})",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = GoldPrimary)
                                            )

                                            // Action Buttons
                                            if (ord.status != OrderStatus.DELIVERED && ord.status != OrderStatus.CANCELLED) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    OutlinedButton(
                                                        onClick = { viewModel.cancelOrder(ord.id) },
                                                        shape = RoundedCornerShape(8.dp),
                                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                        modifier = Modifier.height(34.dp)
                                                    ) {
                                                        Text("Reject", color = NonVegRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }

                                                    Button(
                                                        onClick = { viewModel.advanceOrderStatus(ord.id) },
                                                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Black),
                                                        shape = RoundedCornerShape(8.dp),
                                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                        modifier = Modifier.height(34.dp)
                                                    ) {
                                                        Text("Advance Status", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            item { Spacer(modifier = Modifier.height(20.dp)) }
                        }
                    }
                }

                AdminTab.CUSTOMERS -> {
                    val customerList = listOf(
                        Triple("Yogesh Chhura", "+91 9668612323", "yogeshchhura@gmail.com"),
                        Triple("Priyanka Mohapatra", "+91 9876543210", "priyanka.m@gmail.com"),
                        Triple("Rakesh Sharma", "+91 9437012345", "rakesh.sharma@gmail.com"),
                        Triple("Sunita Nayak", "+91 9123456789", "sunita.nayak@yahoo.com")
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "Registered Customers (${customerList.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        items(customerList) { cust ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = GoldContainer,
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = GoldPrimary,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = cust.first, fontWeight = FontWeight.Bold)
                                        Text(text = cust.second, fontSize = 12.sp, color = GoldPrimary)
                                        Text(text = cust.third, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = VegGreen.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "Verified",
                                            color = VegGreen,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                AdminTab.OFFERS -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Coupons & Discounts (${coupons.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Button(
                                onClick = { showAddCouponDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Black)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Coupon", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(coupons) { cp ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = cp.code, fontWeight = FontWeight.ExtraBold, color = GoldLight, fontSize = 16.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(text = "${cp.discountPercent}% OFF", fontSize = 11.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                                            }
                                            Text(text = cp.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(text = "Min Order: ₹${cp.minOrderAmount.toInt()} • Max Disc: ₹${cp.maxDiscount.toInt()}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }

                                        Switch(
                                            checked = cp.isActive,
                                            onCheckedChange = { viewModel.toggleCouponActive(cp.code) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                AdminTab.REPORTS -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "Sales & Revenue Performance",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Periodic Sales Metrics", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("Today", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("₹4,890", fontWeight = FontWeight.ExtraBold, color = GoldPrimary)
                                        }
                                        Column {
                                            Text("This Week", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("₹34,520", fontWeight = FontWeight.ExtraBold, color = GoldPrimary)
                                        }
                                        Column {
                                            Text("This Month", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("₹1,48,200", fontWeight = FontWeight.ExtraBold, color = GoldPrimary)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Top Selling Biryani & Starters", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    listOf(
                                        "1. Authentic Hyderabadi Zafrani Dum Biryani" to "142 orders",
                                        "2. Royal Nawabi Chicken Dum Biryani" to "118 orders",
                                        "3. Hyderabadi Crispy Chicken 65" to "96 orders",
                                        "4. Chef's Special Royal Matka Biryani" to "84 orders",
                                        "5. Royal Awadhi Mutton Gosht Biryani" to "72 orders"
                                    ).forEach { (dish, count) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(dish, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                            Text(count, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldLight)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Food Dialog
    if (showAddFoodDialog) {
        var foodName by remember { mutableStateOf("") }
        var foodDesc by remember { mutableStateOf("") }
        var foodPrice by remember { mutableStateOf("") }
        var foodDiscountPrice by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf(FoodCategory.CHICKEN_BIRYANI) }
        var isVeg by remember { mutableStateOf(false) }
        var foodImageUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800&auto=format&fit=crop&q=80") }

        AlertDialog(
            onDismissRequest = { showAddFoodDialog = false },
            title = { Text("Add New Food Item", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("Food Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = foodDesc,
                        onValueChange = { foodDesc = it },
                        label = { Text("Description") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = foodPrice,
                            onValueChange = { foodPrice = it },
                            label = { Text("Price (₹)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = foodDiscountPrice,
                            onValueChange = { foodDiscountPrice = it },
                            label = { Text("Discount (₹)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isVeg, onCheckedChange = { isVeg = it })
                        Text(if (isVeg) "Vegetarian" else "Non-Vegetarian")
                    }

                    OutlinedTextField(
                        value = foodImageUrl,
                        onValueChange = { foodImageUrl = it },
                        label = { Text("Image URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val priceVal = foodPrice.toDoubleOrNull() ?: 250.0
                        val discVal = foodDiscountPrice.toDoubleOrNull()
                        if (foodName.isNotBlank()) {
                            viewModel.addFoodItem(
                                FoodItem(
                                    id = "bb_custom_" + UUID.randomUUID().toString().take(6),
                                    name = foodName,
                                    category = selectedCategory,
                                    description = foodDesc.ifBlank { "Royal signature dish freshly prepared with aromatic spices." },
                                    price = priceVal,
                                    discountPrice = discVal,
                                    isVeg = isVeg,
                                    imageUrl = foodImageUrl
                                )
                            )
                            showAddFoodDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Black)
                ) {
                    Text("Add to Catalog", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFoodDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Add Coupon Dialog
    if (showAddCouponDialog) {
        var code by remember { mutableStateOf("") }
        var discountPercent by remember { mutableStateOf("25") }
        var maxDiscount by remember { mutableStateOf("150") }
        var minOrder by remember { mutableStateOf("299") }
        var desc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddCouponDialog = false },
            title = { Text("Create Promotional Coupon", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it.uppercase() },
                        label = { Text("Coupon Code (e.g. FESTIVE30)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = discountPercent,
                            onValueChange = { discountPercent = it },
                            label = { Text("Discount %") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = maxDiscount,
                            onValueChange = { maxDiscount = it },
                            label = { Text("Max Disc (₹)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = minOrder,
                        onValueChange = { minOrder = it },
                        label = { Text("Min Order (₹)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (code.isNotBlank()) {
                            viewModel.addCoupon(
                                Coupon(
                                    code = code,
                                    discountPercent = discountPercent.toIntOrNull() ?: 20,
                                    maxDiscount = maxDiscount.toDoubleOrNull() ?: 100.0,
                                    minOrderAmount = minOrder.toDoubleOrNull() ?: 200.0,
                                    description = desc.ifBlank { "${discountPercent}% OFF up to ₹$maxDiscount on orders above ₹$minOrder" }
                                )
                            )
                            showAddCouponDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Black)
                ) {
                    Text("Save Coupon", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCouponDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Change Restaurant Address Dialog
    if (showEditAddressDialog) {
        var newCurrentAddress by remember { mutableStateOf(restaurantAddress) }

        AlertDialog(
            onDismissRequest = { showEditAddressDialog = false },
            title = { Text("Update Restaurant Address", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Update the active restaurant address. Changes will sync across the entire customer app instantly.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = newCurrentAddress,
                        onValueChange = { newCurrentAddress = it },
                        label = { Text("Restaurant Address") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCurrentAddress.isNotBlank()) {
                            viewModel.updateRestaurantAddress(newCurrentAddress)
                            showEditAddressDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Black)
                ) {
                    Text("Save Address", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditAddressDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AdminMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = color
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            )
        }
    }
}

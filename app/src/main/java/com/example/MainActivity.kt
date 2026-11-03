package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.BiriyaniBarsaTheme
import com.example.ui.theme.Black
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NonVegRed
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BiriyaniBarsaTheme {
                BiriyaniBarsaApp()
            }
        }
    }
}

enum class CustomerNavTab(
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val screen: Screen
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, Screen.Home),
    MENU("Menu", Icons.Filled.RestaurantMenu, Icons.Outlined.RestaurantMenu, Screen.Menu()),
    CART("Cart", Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag, Screen.Cart),
    ORDERS("Orders", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong, Screen.OrdersHistory),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person, Screen.Profile)
}

@Composable
fun BiriyaniBarsaApp(viewModel: MainViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val cartCount by viewModel.cartTotalCount.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.snackbarMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
        }
    }

    // Determine whether to show customer bottom nav bar
    val showCustomerBottomBar = when (currentScreen) {
        is Screen.Home,
        is Screen.Menu,
        is Screen.OrdersHistory,
        is Screen.Profile -> true
        else -> false
    }

    BackHandler(enabled = currentScreen != Screen.Home) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = if (showCustomerBottomBar) 60.dp else 16.dp)
            )
        },
        bottomBar = {
            if (showCustomerBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("customer_bottom_bar")
                ) {
                    CustomerNavTab.entries.forEach { tab ->
                        val isSelected = when (tab) {
                            CustomerNavTab.HOME -> currentScreen is Screen.Home
                            CustomerNavTab.MENU -> currentScreen is Screen.Menu
                            CustomerNavTab.CART -> currentScreen is Screen.Cart
                            CustomerNavTab.ORDERS -> currentScreen is Screen.OrdersHistory
                            CustomerNavTab.PROFILE -> currentScreen is Screen.Profile
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(tab.screen) },
                            icon = {
                                if (tab == CustomerNavTab.CART) {
                                    BadgedBox(
                                        badge = {
                                            if (cartCount > 0) {
                                                Badge(
                                                    containerColor = NonVegRed,
                                                    contentColor = Color.White
                                                ) {
                                                    Text(
                                                        text = "$cartCount",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 10.sp
                                                    )
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.title
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.title
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Black,
                                selectedTextColor = GoldLight,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Home -> {
                    HomeScreen(viewModel = viewModel)
                }
                is Screen.Menu -> {
                    MenuScreen(
                        viewModel = viewModel,
                        initialCategory = screen.category
                    )
                }
                is Screen.Cart -> {
                    CartScreen(viewModel = viewModel)
                }
                is Screen.Checkout -> {
                    CheckoutScreen(viewModel = viewModel)
                }
                is Screen.OrderTracking -> {
                    OrderTrackingScreen(
                        orderId = screen.orderId,
                        viewModel = viewModel
                    )
                }
                is Screen.OrdersHistory -> {
                    OrdersHistoryScreen(viewModel = viewModel)
                }
                is Screen.Profile -> {
                    ProfileScreen(viewModel = viewModel)
                }
                is Screen.Auth -> {
                    AuthScreen(viewModel = viewModel)
                }
                is Screen.AdminLogin -> {
                    AdminLoginScreen(viewModel = viewModel)
                }
                is Screen.AdminDashboard -> {
                    AdminDashboardScreen(viewModel = viewModel)
                }
                is Screen.ContactSupport -> {
                    ProfileScreen(viewModel = viewModel)
                }
            }
        }
    }
}

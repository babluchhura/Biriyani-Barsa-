package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.Screen
import com.example.data.model.FoodCategory
import com.example.ui.components.FoodCard
import com.example.ui.components.VegNonVegBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    viewModel: MainViewModel,
    initialCategory: FoodCategory? = null,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val foodList by viewModel.foodItems.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isVegOnly by viewModel.isVegOnlyFilter.collectAsState()
    val cartCount by viewModel.cartTotalCount.collectAsState()
    val cartSubtotal by viewModel.cartSubtotal.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(initialCategory) {
        if (initialCategory != null) {
            viewModel.selectCategory(initialCategory)
        }
    }

    val cartQuantities = remember(cartItems) {
        cartItems.associate { it.foodItem.id to it.quantity }
    }

    val filteredList = remember(foodList, selectedCategory, isVegOnly, searchQuery) {
        foodList.filter { food ->
            val matchesCategory = (selectedCategory == FoodCategory.ALL) || (food.category == selectedCategory)
            val matchesVeg = !isVegOnly || food.isVeg
            val matchesSearch = searchQuery.isBlank() ||
                    food.name.contains(searchQuery, ignoreCase = true) ||
                    food.description.contains(searchQuery, ignoreCase = true) ||
                    food.category.displayName.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesVeg && matchesSearch
        }
    }

    Scaffold(
        modifier = modifier.testTag("menu_screen"),
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("menu_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Text(
                        text = "Royal Menu & Biryani",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    // Veg Only Toggle Filter
                    FilterChip(
                        selected = isVegOnly,
                        onClick = { viewModel.toggleVegFilter() },
                        label = {
                            Text(
                                text = "Veg Only",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isVegOnly) Black else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingIcon = {
                            VegNonVegBadge(isVeg = true, modifier = Modifier.size(12.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VegGreen,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search within menu...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = GoldPrimary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("menu_search_input")
                )

                // Category chips horizontal carousel
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(FoodCategory.entries) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) GoldLight else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .clickable { viewModel.selectCategory(cat) }
                                .testTag("menu_cat_${cat.name}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = cat.displayName,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                        color = if (isSelected) Black else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Floating View Cart bar when items are in cart
            AnimatedVisibility(
                visible = cartCount > 0,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
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
                                text = "$cartCount ITEMS IN CART",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                            )
                            Text(
                                text = "₹${cartSubtotal.toInt()}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }

                        Button(
                            onClick = { viewModel.navigateTo(Screen.Cart) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                            modifier = Modifier.testTag("floating_view_cart_btn")
                        ) {
                            Text(
                                text = "View Cart",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
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
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedCategory.displayName} (${filteredList.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            }

            if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.RestaurantMenu,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No dishes found matching your filters.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            } else {
                items(filteredList) { food ->
                    FoodCard(
                        food = food,
                        quantityInCart = cartQuantities[food.id] ?: 0,
                        isWishlisted = wishlistIds.contains(food.id),
                        onAddToCart = { viewModel.addToCart(food) },
                        onRemoveFromCart = { viewModel.removeFromCart(food) },
                        onToggleWishlist = { viewModel.toggleWishlist(food.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

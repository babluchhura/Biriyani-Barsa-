package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.MainViewModel
import com.example.Screen
import com.example.data.model.FoodCategory
import com.example.data.repository.RestaurantRepository
import com.example.ui.components.ContactActionBar
import com.example.ui.components.FoodCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val foodList by viewModel.foodItems.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()
    val coupons by viewModel.coupons.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val cartCount by viewModel.cartTotalCount.collectAsState()
    val restaurantAddress by viewModel.restaurantAddress.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val cartQuantities = remember(cartItems) {
        cartItems.associate { it.foodItem.id to it.quantity }
    }

    val bestSellers = remember(foodList) {
        foodList.filter { it.isBestSeller }
    }

    val specials = remember(foodList) {
        foodList.filter { it.isSpecial }
    }

    val recommended = remember(foodList) {
        foodList.filter { it.isRecommended }
    }

    val filteredList = remember(foodList, searchQuery) {
        if (searchQuery.isBlank()) emptyList()
        else foodList.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.category.displayName.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        modifier = modifier.testTag("home_screen"),
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Address header & quick actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo(Screen.Profile) }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "Biriyani Barsa Logo",
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .border(BorderStroke(1.5.dp, GoldPrimary), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "BIRIYANI BARSA",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GoldLight,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }
                            Text(
                                text = "Delivering to ${selectedAddress.locality}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                maxLines = 1
                            )
                        }
                    }

                    // Admin quick portal button & Cart badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Admin quick access button
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.AdminLogin) },
                            modifier = Modifier
                                .background(GoldContainer, CircleShape)
                                .size(38.dp)
                                .testTag("admin_portal_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Portal",
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Cart badge button
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
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        ) {
                            IconButton(
                                onClick = { viewModel.navigateTo(Screen.Cart) },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                    .size(38.dp)
                                    .testTag("top_cart_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBag,
                                    contentDescription = "Cart",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search biryani, chicken 65, kebabs, desserts...",
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
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input")
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Live Search Results Dropdown
            if (searchQuery.isNotBlank()) {
                item {
                    SectionHeader(
                        title = "Search Results (${filteredList.size})",
                        subtitle = "Dishes matching \"$searchQuery\""
                    )
                }

                if (filteredList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No royal dishes match your search.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
            } else {
                // 1. Royal Hero Banner
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                        ) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800&auto=format&fit=crop&q=80",
                                contentDescription = "Hero Biryani Banner",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                BrownDark.copy(alpha = 0.95f),
                                                BrownDark.copy(alpha = 0.75f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = GoldPrimary
                                    ) {
                                        Text(
                                            text = "ESTD. 2026 • ROYAL CUISINE",
                                            color = Black,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "BIRIYANI BARSA",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = GoldLight,
                                            letterSpacing = 1.sp
                                        )
                                    )
                                    Text(
                                        text = "The Royal Dum Biryani Experience",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.navigateTo(Screen.Menu()) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = GoldPrimary,
                                            contentColor = Black
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.RestaurantMenu,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Explore Menu",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            val intent = Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("https://wa.me/919668612323")
                                            )
                                            context.startActivity(intent)
                                        },
                                        border = BorderStroke(1.dp, WhatsAppGreen),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Chat,
                                            contentDescription = null,
                                            tint = WhatsAppGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "WhatsApp",
                                            color = WhatsAppGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // New Location Announcement Banner
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = GoldContainer),
                        border = BorderStroke(1.dp, GoldPrimary)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = GoldPrimary,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = Black,
                                    modifier = Modifier
                                        .padding(9.dp)
                                        .size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "📍 NEW RESTAURANT LOCATION",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = GoldLight,
                                            letterSpacing = 0.5.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = VegGreen
                                    ) {
                                        Text(
                                            text = "OPEN NOW",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = restaurantAddress,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = OnGoldContainer
                                    )
                                )
                            }
                        }
                    }
                }

                // 2. Special Offers & Coupons Carousel
                item {
                    SectionHeader(
                        title = "Special Offers",
                        subtitle = "Exclusive savings on your royal order"
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(coupons) { coupon ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = GoldContainer
                                ),
                                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .width(260.dp)
                                    .clickable {
                                        viewModel.applyCoupon(coupon.code)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = GoldPrimary,
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocalOffer,
                                            contentDescription = null,
                                            tint = Black,
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = coupon.code,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = GoldLight
                                                )
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = "${coupon.discountPercent}% OFF",
                                                    color = GoldLight,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = coupon.description,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = OnGoldContainer
                                            ),
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Category Carousel
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    SectionHeader(
                        title = "Browse Categories",
                        subtitle = "Select your favourite cuisine",
                        actionText = "See All",
                        onActionClick = { viewModel.navigateTo(Screen.Menu()) }
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(FoodCategory.entries.filter { it != FoodCategory.ALL }) { cat ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier
                                    .clickable {
                                        viewModel.selectCategory(cat)
                                        viewModel.navigateTo(Screen.Menu(cat))
                                    }
                                    .testTag("category_chip_${cat.name}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Icon(
                                        imageVector = when (cat) {
                                            FoodCategory.CHICKEN_BIRYANI -> Icons.Default.LunchDining
                                            FoodCategory.MUTTON_BIRYANI -> Icons.Default.DinnerDining
                                            FoodCategory.EGG_BIRYANI -> Icons.Default.Egg
                                            FoodCategory.VEG_BIRYANI -> Icons.Default.Eco
                                            FoodCategory.HYDERABADI_BIRYANI -> Icons.Default.LocalFireDepartment
                                            FoodCategory.SPECIAL_BIRYANI -> Icons.Default.Star
                                            FoodCategory.CHICKEN_65 -> Icons.Default.LocalPizza
                                            FoodCategory.CHICKEN_LOLLIPOP -> Icons.Default.KebabDining
                                            FoodCategory.STARTERS -> Icons.Default.Tapas
                                            FoodCategory.FAST_FOOD -> Icons.Default.Fastfood
                                            FoodCategory.COLD_DRINKS -> Icons.Default.LocalBar
                                            FoodCategory.DESSERTS -> Icons.Default.Icecream
                                            else -> Icons.Default.Restaurant
                                        },
                                        contentDescription = cat.displayName,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = cat.displayName,
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Today's Specials
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    SectionHeader(
                        title = "Today's Special",
                        subtitle = "Handcrafted by our master chef"
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(specials) { food ->
                            Box(modifier = Modifier.width(280.dp)) {
                                FoodCard(
                                    food = food,
                                    quantityInCart = cartQuantities[food.id] ?: 0,
                                    isWishlisted = wishlistIds.contains(food.id),
                                    onAddToCart = { viewModel.addToCart(food) },
                                    onRemoveFromCart = { viewModel.removeFromCart(food) },
                                    onToggleWishlist = { viewModel.toggleWishlist(food.id) }
                                )
                            }
                        }
                    }
                }

                // 5. Best Selling Biryani
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    SectionHeader(
                        title = "Best Selling Biryani",
                        subtitle = "Most loved by our royal patrons",
                        actionText = "View Menu",
                        onActionClick = { viewModel.navigateTo(Screen.Menu(FoodCategory.CHICKEN_BIRYANI)) }
                    )
                }

                items(bestSellers) { food ->
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

                // 6. Recommended For You
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    SectionHeader(
                        title = "Recommended Dishes",
                        subtitle = "Perfect starters & biryani pairings"
                    )
                }

                items(recommended) { food ->
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

                // 7. Customer Reviews Section
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    SectionHeader(
                        title = "Customer Reviews",
                        subtitle = "What biryani lovers say about us"
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(RestaurantRepository.customerReviews) { rev ->
                            Card(
                                modifier = Modifier.width(300.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = rev.customerName,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            repeat(5) {
                                                Icon(
                                                    imageVector = Icons.Filled.Star,
                                                    contentDescription = null,
                                                    tint = StarGold,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Ordered: ${rev.dishName}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = GoldPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "\"${rev.comment}\"",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        maxLines = 4
                                    )
                                }
                            }
                        }
                    }
                }

                // 8. Contact & Information Card
                item {
                    Spacer(modifier = Modifier.height(14.dp))
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

                // 9. Restaurant Information Footer
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Biriyani Barsa - Royal Kitchen",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "📍 Restaurant Address: $restaurantAddress",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🕒 Timings: ${RestaurantRepository.RESTAURANT_TIMINGS}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "✉️ Email: ${RestaurantRepository.CONTACT_EMAIL}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object RestaurantRepository {

    // Restaurant details
    const val RESTAURANT_NAME = "Biriyani Barsa"
    const val RESTAURANT_TAGLINE = "The Royal Dum Biryani Experience"
    const val CONTACT_EMAIL = "yogeshchhura@gmail.com"
    const val CONTACT_WHATSAPP_NUMBER = "+919668612323"
    const val CONTACT_CALL_NUMBER = "+9668612323"
    const val RESTAURANT_ADDRESS = "Main Road, Limpara, Bangomunda, Balangir District, Odisha - 767037"
    const val RESTAURANT_TIMINGS = "11:00 AM - 11:30 PM (Mon - Sun)"

    // Seed Menu Items
    private val initialFoodItems = listOf(
        // Hyderabadi & Special
        FoodItem(
            id = "bb_hyd_01",
            name = "Authentic Hyderabadi Zafrani Dum Biryani",
            category = FoodCategory.HYDERABADI_BIRYANI,
            description = "Slow cooked royal basmati rice infused with pure Kashmiri saffron, tender mutton chunks marinated in authentic Nizam spices & ghee, sealed with dough (dum). Served with Mirchi Ka Salan and creamy Raita.",
            price = 399.0,
            discountPrice = 349.0,
            isVeg = false,
            imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800&auto=format&fit=crop&q=80",
            rating = 4.9f,
            ratingCount = 380,
            isBestSeller = true,
            isSpecial = true,
            isRecommended = true,
            spiceLevel = "Spicy",
            servingSize = "Serves 1-2"
        ),
        FoodItem(
            id = "bb_sp_01",
            name = "Chef's Special Royal Matka Biryani",
            category = FoodCategory.SPECIAL_BIRYANI,
            description = "Signature layered clay pot biryani cooked on slow charcoal embers with succulent chicken, boiled eggs, fried cashews, caramelized barista onions, and silver vark finish.",
            price = 449.0,
            discountPrice = 389.0,
            isVeg = false,
            imageUrl = "https://images.unsplash.com/photo-1589302168068-964664d93dc0?w=800&auto=format&fit=crop&q=80",
            rating = 4.9f,
            ratingCount = 295,
            isBestSeller = true,
            isSpecial = true,
            isRecommended = true,
            spiceLevel = "Medium Spicy",
            servingSize = "Serves 2"
        ),

        // Chicken Biryani
        FoodItem(
            id = "bb_chk_01",
            name = "Royal Nawabi Chicken Dum Biryani",
            category = FoodCategory.CHICKEN_BIRYANI,
            description = "Fragrant long-grain basmati rice cooked with bone-in tender chicken pieces, fresh mint, coriander, and secret Barsa spices in clarified desi ghee.",
            price = 280.0,
            discountPrice = 249.0,
            isVeg = false,
            imageUrl = "https://images.unsplash.com/photo-1633945274405-b6c8069047b0?w=800&auto=format&fit=crop&q=80",
            rating = 4.8f,
            ratingCount = 512,
            isBestSeller = true,
            isRecommended = true,
            spiceLevel = "Medium Spicy",
            servingSize = "Serves 1-2"
        ),
        FoodItem(
            id = "bb_chk_02",
            name = "Kolkata Style Chicken Biryani with Egg & Aloo",
            category = FoodCategory.CHICKEN_BIRYANI,
            description = "Aromatic mild spiced delicacy with melting soft spiced potato, boiled egg, tender chicken leg piece, and soothing rose and kewra essence.",
            price = 270.0,
            discountPrice = 239.0,
            isVeg = false,
            imageUrl = "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?w=800&auto=format&fit=crop&q=80",
            rating = 4.7f,
            ratingCount = 210,
            isBestSeller = false,
            spiceLevel = "Mild",
            servingSize = "Serves 1-2"
        ),
        FoodItem(
            id = "bb_chk_03",
            name = "Boneless Chicken Tikka Biryani",
            category = FoodCategory.CHICKEN_BIRYANI,
            description = "Smoky tandoor-roasted boneless chicken tikka tossed with rich spicy masala and layered with saffron-tinged biryani rice.",
            price = 320.0,
            discountPrice = 289.0,
            isVeg = false,
            imageUrl = "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=800&auto=format&fit=crop&q=80",
            rating = 4.8f,
            ratingCount = 180,
            isRecommended = true,
            spiceLevel = "Spicy",
            servingSize = "Serves 1"
        ),

        // Mutton Biryani
        FoodItem(
            id = "bb_mut_01",
            name = "Royal Awadhi Mutton Gosht Biryani",
            category = FoodCategory.MUTTON_BIRYANI,
            description = "Prime cuts of succulent tender mutton slow-braised for hours in rich yakhni stock and layered with royal basmati rice and mace-cardamom blend.",
            price = 450.0,
            discountPrice = 399.0,
            isVeg = false,
            imageUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?w=800&auto=format&fit=crop&q=80",
            rating = 4.9f,
            ratingCount = 340,
            isBestSeller = true,
            isSpecial = true,
            isRecommended = true,
            spiceLevel = "Medium Spicy",
            servingSize = "Serves 1-2"
        ),

        // Egg Biryani
        FoodItem(
            id = "bb_egg_01",
            name = "Golden Roasted Double Egg Dum Biryani",
            category = FoodCategory.EGG_BIRYANI,
            description = "Two golden turmeric-fried eggs tossed in rich onion-tomato masala gravy and steamed with spiced basmati dum rice.",
            price = 210.0,
            discountPrice = 179.0,
            isVeg = false,
            imageUrl = "https://images.unsplash.com/photo-1512058564366-18510be2db19?w=800&auto=format&fit=crop&q=80",
            rating = 4.6f,
            ratingCount = 165,
            spiceLevel = "Medium Spicy",
            servingSize = "Serves 1"
        ),

        // Veg Biryani
        FoodItem(
            id = "bb_veg_01",
            name = "Shahi Paneer Tikka Dum Biryani",
            category = FoodCategory.VEG_BIRYANI,
            description = "Creamy marinated malai paneer cubes charred in tandoor, folded into saffron rice with green peas, mint, and toasted golden cashews.",
            price = 240.0,
            discountPrice = 209.0,
            isVeg = true,
            imageUrl = "https://images.unsplash.com/photo-1645177628172-a94c1f96e6db?w=800&auto=format&fit=crop&q=80",
            rating = 4.7f,
            ratingCount = 230,
            isRecommended = true,
            spiceLevel = "Mild to Medium",
            servingSize = "Serves 1-2"
        ),
        FoodItem(
            id = "bb_veg_02",
            name = "Hyderabadi Subz Dum Biryani",
            category = FoodCategory.VEG_BIRYANI,
            description = "Fresh farm garden vegetables, french beans, carrots, potatoes, and cauliflower infused in aromatic spices and slow dum cooked.",
            price = 220.0,
            discountPrice = 189.0,
            isVeg = true,
            imageUrl = "https://images.unsplash.com/photo-1567337710282-00832b415979?w=800&auto=format&fit=crop&q=80",
            rating = 4.6f,
            ratingCount = 190,
            spiceLevel = "Medium",
            servingSize = "Serves 1-2"
        ),

        // Chicken 65 & Lollipop
        FoodItem(
            id = "bb_c65_01",
            name = "Hyderabadi Crispy Chicken 65",
            category = FoodCategory.CHICKEN_65,
            description = "Deep fried spicy boneless chicken cubes tossed with crisp curry leaves, slit green chilies, crushed black pepper, and tangy yogurt reduction.",
            price = 220.0,
            discountPrice = 189.0,
            isVeg = false,
            imageUrl = "https://images.unsplash.com/photo-1610057099443-fde8c4d50f91?w=800&auto=format&fit=crop&q=80",
            rating = 4.8f,
            ratingCount = 420,
            isBestSeller = true,
            isRecommended = true,
            spiceLevel = "Extra Spicy",
            servingSize = "8 pieces"
        ),
        FoodItem(
            id = "bb_lol_01",
            name = "Crispy Spicy Chicken Lollipop",
            category = FoodCategory.CHICKEN_LOLLIPOP,
            description = "Signature frenched chicken winglets marinated in fiery red spices, fried till ultra-crisp and served with hot Schezwan dipping sauce.",
            price = 240.0,
            discountPrice = 219.0,
            isVeg = false,
            imageUrl = "https://images.unsplash.com/photo-1527477378378-b11c6d86241b?w=800&auto=format&fit=crop&q=80",
            rating = 4.7f,
            ratingCount = 310,
            isBestSeller = true,
            spiceLevel = "Spicy",
            servingSize = "6 pieces"
        ),

        // Starters
        FoodItem(
            id = "bb_str_01",
            name = "Tandoori Tangdi Chicken Kebab",
            category = FoodCategory.STARTERS,
            description = "Juicy chicken drumsticks marinated overnight in thick Greek hung curd, mustard oil, and kashmiri deggi mirch, charcoal charred to perfection.",
            price = 260.0,
            discountPrice = 229.0,
            isVeg = false,
            imageUrl = "https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?w=800&auto=format&fit=crop&q=80",
            rating = 4.8f,
            ratingCount = 280,
            isSpecial = true,
            spiceLevel = "Medium Spicy",
            servingSize = "4 drumsticks"
        ),
        FoodItem(
            id = "bb_str_02",
            name = "Golden Crispy Pepper Corn",
            category = FoodCategory.STARTERS,
            description = "Crunchy batter-fried American sweet corn kernels tossed with diced bell peppers, scallions, roasted cumin, and black pepper.",
            price = 180.0,
            discountPrice = 149.0,
            isVeg = true,
            imageUrl = "https://images.unsplash.com/photo-1551782450-a2132b4ba21d?w=800&auto=format&fit=crop&q=80",
            rating = 4.6f,
            ratingCount = 140,
            spiceLevel = "Mild",
            servingSize = "Plate"
        ),

        // Fast Food
        FoodItem(
            id = "bb_fst_01",
            name = "Barsa Royal Chicken Kathi Roll",
            category = FoodCategory.FAST_FOOD,
            description = "Flaky buttery lachha paratha layered with egg, loaded with smoky chicken tikka chunks, sliced onions, tangy mint chutney, and chaat masala.",
            price = 160.0,
            discountPrice = 139.0,
            isVeg = false,
            imageUrl = "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?w=800&auto=format&fit=crop&q=80",
            rating = 4.7f,
            ratingCount = 250,
            isBestSeller = true,
            spiceLevel = "Medium",
            servingSize = "1 Roll"
        ),
        FoodItem(
            id = "bb_fst_02",
            name = "Masala Peri-Peri Biryani Fries",
            category = FoodCategory.FAST_FOOD,
            description = "Crispy golden potato fingers dusted with house-blended peri-peri spices and biryani masala powder.",
            price = 120.0,
            discountPrice = 99.0,
            isVeg = true,
            imageUrl = "https://images.unsplash.com/photo-1576107232684-1279f3908594?w=800&auto=format&fit=crop&q=80",
            rating = 4.5f,
            ratingCount = 180,
            spiceLevel = "Spicy",
            servingSize = "Large"
        ),

        // Cold Drinks
        FoodItem(
            id = "bb_drk_01",
            name = "Royal Rose Zafrani Falooda",
            category = FoodCategory.COLD_DRINKS,
            description = "Chilled rich evaporated milk flavored with damask rose syrup, vermicelli, sweet basil seeds (sabja), topped with vanilla ice cream and pistachios.",
            price = 140.0,
            discountPrice = 119.0,
            isVeg = true,
            imageUrl = "https://images.unsplash.com/photo-1551024709-8f23befc6f87?w=800&auto=format&fit=crop&q=80",
            rating = 4.9f,
            ratingCount = 310,
            isSpecial = true,
            spiceLevel = "Sweet",
            servingSize = "350 ml"
        ),
        FoodItem(
            id = "bb_drk_02",
            name = "Fresh Mint Masala Shikanji Soda",
            category = FoodCategory.COLD_DRINKS,
            description = "Crisp sparkling soda muddled with fresh mint leaves, squeezed lemons, roasted cumin, black salt, and ginger syrup. Perfect biryani companion!",
            price = 80.0,
            discountPrice = 69.0,
            isVeg = true,
            imageUrl = "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=800&auto=format&fit=crop&q=80",
            rating = 4.8f,
            ratingCount = 220,
            spiceLevel = "Tangy & Refreshing",
            servingSize = "300 ml"
        ),

        // Desserts
        FoodItem(
            id = "bb_dst_01",
            name = "Shahi Tukda with Thick Rabri",
            category = FoodCategory.DESSERTS,
            description = "Royal Mughlai dessert of ghee-fried bread triangles soaked in warm saffron syrup, blanketed in creamy malai rabri, toasted slivered almonds, and silver foil.",
            price = 150.0,
            discountPrice = 129.0,
            isVeg = true,
            imageUrl = "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=800&auto=format&fit=crop&q=80",
            rating = 4.9f,
            ratingCount = 380,
            isSpecial = true,
            isRecommended = true,
            spiceLevel = "Sweet",
            servingSize = "2 pieces"
        ),
        FoodItem(
            id = "bb_dst_02",
            name = "Hot Desi Ghee Gulab Jamun (2 Pcs)",
            category = FoodCategory.DESSERTS,
            description = "Soft melt-in-mouth khoya dumplings soaked in fragrant green cardamom and saffron sugar syrup, garnished with crushed green pistachio nuts.",
            price = 90.0,
            discountPrice = 79.0,
            isVeg = true,
            imageUrl = "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=800&auto=format&fit=crop&q=80",
            rating = 4.8f,
            ratingCount = 410,
            spiceLevel = "Sweet",
            servingSize = "2 pieces"
        )
    )

    // Coupons
    private val initialCoupons = listOf(
        Coupon("BARSA50", 50, 150.0, 299.0, "50% OFF up to ₹150 on orders above ₹299"),
        Coupon("FLAT20", 20, 200.0, 399.0, "Flat 20% OFF on premium biryani feasts above ₹399"),
        Coupon("WELCOME", 30, 100.0, 199.0, "Flat ₹100 Welcome savings for new royalty foodies"),
        Coupon("FREESHIP", 100, 40.0, 250.0, "Free doorstep express delivery above ₹250")
    )

    // Customer Reviews
    val customerReviews = listOf(
        CustomerReview(
            id = "rev_1",
            customerName = "Priyanka Mohapatra",
            rating = 5.0f,
            comment = "Without doubt the best Biryani in the city! The aroma when opening the sealed handi pot was heavenly, perfectly cooked succulent chicken and authentic zafrani fragrance.",
            dishName = "Authentic Hyderabadi Dum Biryani",
            date = "Yesterday"
        ),
        CustomerReview(
            id = "rev_2",
            customerName = "Rakesh Sharma",
            rating = 5.0f,
            comment = "Chicken 65 is crispy and spicy just as advertised. Delivered steaming hot in 25 minutes. Biriyani Barsa is now our family's weekly Sunday ritual!",
            dishName = "Chicken 65 & Matka Biryani",
            date = "3 days ago"
        ),
        CustomerReview(
            id = "rev_3",
            customerName = "Sunita Nayak",
            rating = 4.9f,
            comment = "Shahi Paneer Tikka Biryani is a vegetarian dream come true. Juicy paneer and fragrant rice. Shahi Tukda is also a must-try dessert.",
            dishName = "Shahi Paneer Tikka Dum Biryani",
            date = "5 days ago"
        )
    )

    // Seed default addresses in Limpara & Bangomunda
    val defaultAddresses = listOf(
        DeliveryAddress(
            id = "addr_1",
            label = "Home",
            street = "House No. 24, Main Road",
            locality = "Limpara",
            landmark = "Near Royal Shiv Mandir",
            city = "Bangomunda, Balangir",
            pincode = "767037",
            isDefault = true
        ),
        DeliveryAddress(
            id = "addr_2",
            label = "Office",
            street = "Shop 12, Market Square",
            locality = "Bangomunda",
            landmark = "Opposite Daily Bus Stand",
            city = "Bangomunda, Balangir",
            pincode = "767037",
            isDefault = false
        )
    )

    // Dynamic Restaurant Address State
    private val _restaurantAddress = MutableStateFlow("Main Road, Limpara, Bangomunda, Balangir District, Odisha - 767037")
    val restaurantAddress: StateFlow<String> = _restaurantAddress.asStateFlow()

    fun updateRestaurantAddress(currentAddr: String) {
        _restaurantAddress.value = currentAddr
    }

    // State flows
    private val _foodItems = MutableStateFlow(initialFoodItems)
    val foodItems: StateFlow<List<FoodItem>> = _foodItems.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _appliedCoupon = MutableStateFlow<Coupon?>(null)
    val appliedCoupon: StateFlow<Coupon?> = _appliedCoupon.asStateFlow()

    private val _coupons = MutableStateFlow(initialCoupons)
    val coupons: StateFlow<List<Coupon>> = _coupons.asStateFlow()

    private val _savedAddresses = MutableStateFlow(defaultAddresses)
    val savedAddresses: StateFlow<List<DeliveryAddress>> = _savedAddresses.asStateFlow()

    private val _selectedAddress = MutableStateFlow(defaultAddresses.first())
    val selectedAddress: StateFlow<DeliveryAddress> = _selectedAddress.asStateFlow()

    // Pre-populate some completed and active orders for realistic experience
    private val initialOrders = listOf(
        Order(
            id = "BB-9241",
            items = listOf(
                CartItem(initialFoodItems[0], 2),
                CartItem(initialFoodItems[9], 1)
            ),
            subtotal = 887.0,
            discount = 150.0,
            deliveryFee = 0.0,
            taxGst = 36.85,
            packagingFee = 20.0,
            grandTotal = 793.85,
            address = defaultAddresses[0],
            paymentMethod = PaymentMethod.UPI_PAYMENT,
            status = OrderStatus.FOOD_PREPARING,
            customerName = "Yogesh Chhura",
            customerPhone = "+91 9668612323",
            cookingNotes = "Extra mirchi salan and fried onions please!"
        ),
        Order(
            id = "BB-8812",
            items = listOf(
                CartItem(initialFoodItems[2], 1),
                CartItem(initialFoodItems[13], 1)
            ),
            subtotal = 368.0,
            discount = 50.0,
            deliveryFee = 40.0,
            taxGst = 17.9,
            packagingFee = 20.0,
            grandTotal = 395.9,
            address = defaultAddresses[0],
            paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
            status = OrderStatus.DELIVERED,
            orderTimestamp = System.currentTimeMillis() - 86400000L * 2,
            customerName = "Yogesh Chhura",
            customerPhone = "+91 9668612323"
        )
    )

    private val _orders = MutableStateFlow(initialOrders)
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _currentUser = MutableStateFlow(
        User(
            id = "usr_001",
            name = "Yogesh Chhura",
            email = "yogeshchhura@gmail.com",
            phone = "+91 9668612323",
            role = UserRole.CUSTOMER,
            addresses = defaultAddresses
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    private val _wishlistIds = MutableStateFlow<Set<String>>(setOf("bb_hyd_01", "bb_c65_01"))
    val wishlistIds: StateFlow<Set<String>> = _wishlistIds.asStateFlow()

    // Cart Operations
    fun addToCart(item: FoodItem) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.foodItem.id == item.id }
        if (index >= 0) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current.add(CartItem(foodItem = item, quantity = 1))
        }
        _cartItems.value = current
    }

    fun removeFromCart(item: FoodItem) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.foodItem.id == item.id }
        if (index >= 0) {
            val existing = current[index]
            if (existing.quantity > 1) {
                current[index] = existing.copy(quantity = existing.quantity - 1)
            } else {
                current.removeAt(index)
            }
            _cartItems.value = current
        }
    }

    fun deleteFromCart(item: FoodItem) {
        _cartItems.value = _cartItems.value.filter { it.foodItem.id != item.id }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _appliedCoupon.value = null
    }

    fun applyCoupon(code: String): Boolean {
        val found = _coupons.value.find { it.code.equals(code.trim(), ignoreCase = true) && it.isActive }
        return if (found != null) {
            _appliedCoupon.value = found
            true
        } else {
            false
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
    }

    fun toggleWishlist(itemId: String) {
        val current = _wishlistIds.value.toMutableSet()
        if (current.contains(itemId)) {
            current.remove(itemId)
        } else {
            current.add(itemId)
        }
        _wishlistIds.value = current
    }

    // Address Operations
    fun selectAddress(address: DeliveryAddress) {
        _selectedAddress.value = address
    }

    fun addAddress(address: DeliveryAddress) {
        val updated = _savedAddresses.value + address
        _savedAddresses.value = updated
        _selectedAddress.value = address
    }

    fun deleteAddress(id: String) {
        val updated = _savedAddresses.value.filter { it.id != id }
        _savedAddresses.value = updated
        if (_selectedAddress.value.id == id && updated.isNotEmpty()) {
            _selectedAddress.value = updated.first()
        }
    }

    // Place Order
    fun placeOrder(
        paymentMethod: PaymentMethod,
        cookingNotes: String
    ): Order {
        val items = _cartItems.value
        val subtotal = items.sumOf { it.itemTotal }
        val coupon = _appliedCoupon.value
        val discount = if (coupon != null && subtotal >= coupon.minOrderAmount) {
            val calc = (subtotal * coupon.discountPercent) / 100.0
            calc.coerceAtMost(coupon.maxDiscount)
        } else 0.0

        val deliveryFee = if (subtotal > 499.0 || (coupon?.code == "FREESHIP")) 0.0 else 40.0
        val taxGst = (subtotal - discount).coerceAtLeast(0.0) * 0.05
        val packagingFee = 20.0
        val grandTotal = (subtotal - discount + deliveryFee + taxGst + packagingFee).coerceAtLeast(0.0)

        val newOrder = Order(
            id = "BB-" + (1000..9999).random(),
            items = items,
            subtotal = subtotal,
            discount = discount,
            deliveryFee = deliveryFee,
            taxGst = taxGst,
            packagingFee = packagingFee,
            grandTotal = grandTotal,
            address = _selectedAddress.value,
            paymentMethod = paymentMethod,
            status = OrderStatus.ORDER_PLACED,
            orderTimestamp = System.currentTimeMillis(),
            customerName = _currentUser.value.name,
            customerPhone = _currentUser.value.phone,
            cookingNotes = cookingNotes
        )

        _orders.value = listOf(newOrder) + _orders.value
        clearCart()
        return newOrder
    }

    // Admin & Tracking: Advance Order Status
    fun advanceOrderStatus(orderId: String, newStatus: OrderStatus? = null) {
        val currentOrders = _orders.value.toMutableList()
        val index = currentOrders.indexOfFirst { it.id == orderId }
        if (index >= 0) {
            val order = currentOrders[index]
            val updatedStatus = newStatus ?: when (order.status) {
                OrderStatus.ORDER_PLACED -> OrderStatus.ORDER_ACCEPTED
                OrderStatus.ORDER_ACCEPTED -> OrderStatus.FOOD_PREPARING
                OrderStatus.FOOD_PREPARING -> OrderStatus.READY_FOR_DELIVERY
                OrderStatus.READY_FOR_DELIVERY -> OrderStatus.OUT_FOR_DELIVERY
                OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
                OrderStatus.DELIVERED -> OrderStatus.DELIVERED
                OrderStatus.CANCELLED -> OrderStatus.CANCELLED
            }
            currentOrders[index] = order.copy(status = updatedStatus)
            _orders.value = currentOrders
        }
    }

    fun cancelOrder(orderId: String) {
        advanceOrderStatus(orderId, OrderStatus.CANCELLED)
    }

    // Admin Food Management
    fun addFoodItem(item: FoodItem) {
        _foodItems.value = listOf(item) + _foodItems.value
    }

    fun updateFoodItem(item: FoodItem) {
        _foodItems.value = _foodItems.value.map { if (it.id == item.id) item else it }
    }

    fun deleteFoodItem(id: String) {
        _foodItems.value = _foodItems.value.filter { it.id != id }
    }

    fun toggleFoodAvailability(id: String) {
        _foodItems.value = _foodItems.value.map {
            if (it.id == id) it.copy(isAvailable = !it.isAvailable) else it
        }
    }

    // Admin Coupons
    fun addCoupon(coupon: Coupon) {
        _coupons.value = listOf(coupon) + _coupons.value
    }

    fun toggleCouponActive(code: String) {
        _coupons.value = _coupons.value.map {
            if (it.code == code) it.copy(isActive = !it.isActive) else it
        }
    }

    // Auth & Security
    fun loginCustomer(phoneOrEmail: String, name: String = "Valued Customer") {
        _currentUser.value = User(
            id = "usr_" + UUID.randomUUID().toString().take(6),
            name = if (name.isNotBlank()) name else "Biryani Lover",
            email = if (phoneOrEmail.contains("@")) phoneOrEmail else "customer@biriyanibarsa.com",
            phone = if (!phoneOrEmail.contains("@")) phoneOrEmail else "+91 9668612323",
            role = UserRole.CUSTOMER,
            addresses = _savedAddresses.value
        )
    }

    fun updateProfile(name: String, email: String, phone: String) {
        _currentUser.value = _currentUser.value.copy(
            name = name,
            email = email,
            phone = phone
        )
    }

    fun loginAdmin(password: String, email: String): Boolean {
        // Strict role-based check
        val validEmail = email.trim().equals("admin@biriyanibarsa.com", ignoreCase = true) ||
                email.trim().equals("yogeshchhura@gmail.com", ignoreCase = true)
        val validPass = password.trim() == "admin123" || password.trim() == "barsa@2026"

        return if (validEmail && validPass) {
            _isAdminLoggedIn.value = true
            _currentUser.value = _currentUser.value.copy(role = UserRole.ADMIN)
            true
        } else {
            false
        }
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
        _currentUser.value = _currentUser.value.copy(role = UserRole.CUSTOMER)
    }
}

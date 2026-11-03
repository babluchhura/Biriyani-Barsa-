package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    data object Home : Screen()
    data class Menu(val category: FoodCategory? = null) : Screen()
    data object Cart : Screen()
    data object Checkout : Screen()
    data class OrderTracking(val orderId: String) : Screen()
    data object OrdersHistory : Screen()
    data object Profile : Screen()
    data object Auth : Screen()
    data object AdminLogin : Screen()
    data object AdminDashboard : Screen()
    data object ContactSupport : Screen()
}

class MainViewModel : ViewModel() {

    private val repository = RestaurantRepository

    // Navigation Stack
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Screen Backstack
    private val screenBackStack = mutableListOf<Screen>(Screen.Home)

    // Data from Repository
    val foodItems = repository.foodItems
    val cartItems = repository.cartItems
    val appliedCoupon = repository.appliedCoupon
    val coupons = repository.coupons
    val savedAddresses = repository.savedAddresses
    val selectedAddress = repository.selectedAddress
    val orders = repository.orders
    val currentUser = repository.currentUser
    val isAdminLoggedIn = repository.isAdminLoggedIn
    val wishlistIds = repository.wishlistIds
    val restaurantAddress = repository.restaurantAddress

    fun updateRestaurantAddress(currentAddr: String) {
        repository.updateRestaurantAddress(currentAddr)
        emitMessage("Restaurant address updated successfully")
    }

    // Menu Screen Filter States
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(FoodCategory.ALL)
    val selectedCategory: StateFlow<FoodCategory> = _selectedCategory.asStateFlow()

    private val _isVegOnlyFilter = MutableStateFlow(false)
    val isVegOnlyFilter: StateFlow<Boolean> = _isVegOnlyFilter.asStateFlow()

    // Notification / Toast Message
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    // Active Tracking Order ID
    private val _activeTrackingOrderId = MutableStateFlow<String?>(null)
    val activeTrackingOrderId: StateFlow<String?> = _activeTrackingOrderId.asStateFlow()

    // Computed cart values
    val cartTotalCount: StateFlow<Int> = cartItems.map { list ->
        list.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartSubtotal: StateFlow<Double> = cartItems.map { list ->
        list.sumOf { it.itemTotal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenBackStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenBackStack.size > 1) {
            screenBackStack.removeAt(screenBackStack.size - 1)
            _currentScreen.value = screenBackStack.last()
            return true
        } else if (_currentScreen.value != Screen.Home) {
            _currentScreen.value = Screen.Home
            screenBackStack.clear()
            screenBackStack.add(Screen.Home)
            return true
        }
        return false
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: FoodCategory) {
        _selectedCategory.value = category
    }

    fun toggleVegFilter() {
        _isVegOnlyFilter.value = !_isVegOnlyFilter.value
    }

    fun addToCart(item: FoodItem) {
        repository.addToCart(item)
        emitMessage("Added ${item.name} to cart")
    }

    fun removeFromCart(item: FoodItem) {
        repository.removeFromCart(item)
    }

    fun deleteFromCart(item: FoodItem) {
        repository.deleteFromCart(item)
        emitMessage("Removed ${item.name}")
    }

    fun clearCart() {
        repository.clearCart()
    }

    fun applyCoupon(code: String) {
        val success = repository.applyCoupon(code)
        if (success) {
            emitMessage("Coupon '$code' applied successfully!")
        } else {
            emitMessage("Invalid coupon code or minimum requirement not met")
        }
    }

    fun removeCoupon() {
        repository.removeCoupon()
        emitMessage("Coupon removed")
    }

    fun toggleWishlist(itemId: String) {
        repository.toggleWishlist(itemId)
    }

    fun selectAddress(address: DeliveryAddress) {
        repository.selectAddress(address)
    }

    fun addAddress(address: DeliveryAddress) {
        repository.addAddress(address)
        emitMessage("New delivery address added")
    }

    fun deleteAddress(id: String) {
        repository.deleteAddress(id)
        emitMessage("Address removed")
    }

    fun placeOrder(paymentMethod: PaymentMethod, cookingNotes: String) {
        val order = repository.placeOrder(paymentMethod, cookingNotes)
        _activeTrackingOrderId.value = order.id
        emitMessage("🎉 Royal Feast Order ${order.id} placed successfully!")
        navigateTo(Screen.OrderTracking(order.id))
    }

    fun trackOrder(orderId: String) {
        _activeTrackingOrderId.value = orderId
        navigateTo(Screen.OrderTracking(orderId))
    }

    fun advanceOrderStatus(orderId: String, newStatus: OrderStatus? = null) {
        repository.advanceOrderStatus(orderId, newStatus)
        emitMessage("Order $orderId status updated")
    }

    fun cancelOrder(orderId: String) {
        repository.cancelOrder(orderId)
        emitMessage("Order $orderId has been cancelled")
    }

    fun loginCustomer(phoneOrEmail: String, name: String) {
        repository.loginCustomer(phoneOrEmail, name)
        emitMessage("Welcome to Biriyani Barsa, ${name.ifBlank { "Royal Guest" }}!")
        navigateBack()
    }

    fun updateProfile(name: String, email: String, phone: String) {
        repository.updateProfile(name, email, phone)
        emitMessage("Profile updated successfully")
    }

    fun loginAdmin(password: String, email: String): Boolean {
        val success = repository.loginAdmin(password, email)
        if (success) {
            emitMessage("Admin authentication verified. Welcome, Manager!")
            navigateTo(Screen.AdminDashboard)
        } else {
            emitMessage("Access Denied: Invalid administrator credentials")
        }
        return success
    }

    fun logoutAdmin() {
        repository.logoutAdmin()
        emitMessage("Logged out of Admin Portal")
        navigateTo(Screen.Home)
    }

    // Admin Food CRUD
    fun addFoodItem(item: FoodItem) {
        repository.addFoodItem(item)
        emitMessage("Added ${item.name} to menu catalog")
    }

    fun updateFoodItem(item: FoodItem) {
        repository.updateFoodItem(item)
        emitMessage("Updated ${item.name}")
    }

    fun deleteFoodItem(id: String) {
        repository.deleteFoodItem(id)
        emitMessage("Food item removed from catalog")
    }

    fun toggleFoodAvailability(id: String) {
        repository.toggleFoodAvailability(id)
    }

    fun addCoupon(coupon: Coupon) {
        repository.addCoupon(coupon)
        emitMessage("Coupon code ${coupon.code} created")
    }

    fun toggleCouponActive(code: String) {
        repository.toggleCouponActive(code)
    }

    fun emitMessage(msg: String) {
        viewModelScope.launch {
            _snackbarMessage.emit(msg)
        }
    }
}

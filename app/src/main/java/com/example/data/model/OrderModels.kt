package com.example.data.model

enum class OrderStatus(val title: String, val description: String, val stepIndex: Int) {
    ORDER_PLACED("Order Placed", "Your order has been received by Biriyani Barsa", 0),
    ORDER_ACCEPTED("Order Accepted", "Restaurant accepted your order & started verification", 1),
    FOOD_PREPARING("Food Preparing", "Our master chef is dum cooking fresh biryani", 2),
    READY_FOR_DELIVERY("Ready for Delivery", "Piping hot food is packed in royal sealed handis", 3),
    OUT_FOR_DELIVERY("Out for Delivery", "Rider is heading to your doorstep", 4),
    DELIVERED("Delivered", "Delivered hot and fresh! Enjoy your royal meal", 5),
    CANCELLED("Cancelled", "Order was cancelled", -1)
}

enum class PaymentMethod(val title: String, val iconName: String) {
    CASH_ON_DELIVERY("Cash on Delivery (COD)", "payments"),
    UPI_PAYMENT("UPI (GPay / PhonePe / Paytm)", "qr_code"),
    ONLINE_CARD("Debit / Credit Card & Netbanking", "credit_card")
}

data class DeliveryAddress(
    val id: String,
    val label: String = "Home",
    val street: String,
    val locality: String,
    val city: String,
    val pincode: String,
    val landmark: String = "",
    val isDefault: Boolean = false
) {
    val fullFormattedAddress: String
        get() = "$street, $locality, Near $landmark, $city - $pincode".replace("Near , ", "")
}

data class Order(
    val id: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val discount: Double,
    val deliveryFee: Double,
    val taxGst: Double,
    val packagingFee: Double,
    val grandTotal: Double,
    val address: DeliveryAddress,
    val paymentMethod: PaymentMethod,
    var status: OrderStatus = OrderStatus.ORDER_PLACED,
    val orderTimestamp: Long = System.currentTimeMillis(),
    val estimatedMinutes: Int = 35,
    val customerName: String,
    val customerPhone: String,
    val cookingNotes: String = "",
    val driverName: String = "Imran Khan",
    val driverPhone: String = "+91 9668612323"
)

data class Coupon(
    val code: String,
    val discountPercent: Int,
    val maxDiscount: Double,
    val minOrderAmount: Double,
    val description: String,
    val isActive: Boolean = true
)

enum class UserRole {
    CUSTOMER,
    ADMIN
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: UserRole = UserRole.CUSTOMER,
    val addresses: List<DeliveryAddress> = emptyList()
)

data class CustomerReview(
    val id: String,
    val customerName: String,
    val rating: Float,
    val comment: String,
    val dishName: String,
    val date: String
)

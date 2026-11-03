package com.example.data.model

enum class FoodCategory(val displayName: String, val iconName: String) {
    ALL("All Dishes", "restaurant"),
    CHICKEN_BIRYANI("Chicken Biryani", "lunch_dining"),
    MUTTON_BIRYANI("Mutton Biryani", "dinner_dining"),
    EGG_BIRYANI("Egg Biryani", "egg"),
    VEG_BIRYANI("Veg Biryani", "eco"),
    HYDERABADI_BIRYANI("Hyderabadi Biryani", "local_fire_department"),
    SPECIAL_BIRYANI("Special Biryani", "star"),
    CHICKEN_65("Chicken 65", "local_pizza"),
    CHICKEN_LOLLIPOP("Chicken Lollipop", "kebab_dining"),
    STARTERS("Starters", "tapas"),
    FAST_FOOD("Fast Food", "fastfood"),
    COLD_DRINKS("Cold Drinks", "local_bar"),
    DESSERTS("Desserts", "icecream")
}

data class FoodItem(
    val id: String,
    val name: String,
    val category: FoodCategory,
    val description: String,
    val price: Double,
    val discountPrice: Double? = null,
    val isVeg: Boolean,
    val imageUrl: String,
    val isAvailable: Boolean = true,
    val rating: Float = 4.8f,
    val ratingCount: Int = 120,
    val isBestSeller: Boolean = false,
    val isSpecial: Boolean = false,
    val isRecommended: Boolean = false,
    val spiceLevel: String = "Medium Spicy",
    val servingSize: String = "Serves 1-2"
) {
    val finalPrice: Double
        get() = discountPrice ?: price

    val discountPercentage: Int
        get() = if (discountPrice != null && discountPrice < price) {
            (((price - discountPrice) / price) * 100).toInt()
        } else 0
}

data class CartItem(
    val foodItem: FoodItem,
    val quantity: Int = 1
) {
    val itemTotal: Double
        get() = foodItem.finalPrice * quantity
}

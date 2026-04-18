package com.example.admin

data class OrderData(
    val orderId: String? = null,
    val userId: String? = null,
    val items: List<OrderItem>? = null,
    val totalAmount: String? = null,
    val paymentMethod: String? = null,
    var status: String? = "Placed",
    val timestamp: Long? = null
)

data class OrderItem(
    val id: String? = null,
    val name: String? = null,
    val price: String? = null,
    val image: String? = null,
    val quantity: Int = 1
)

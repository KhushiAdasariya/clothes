package com.example.clothy

import com.google.firebase.database.PropertyName

data class AddressModel(
    val id: String? = null,
    val name: String? = null,
    val mobile: String? = null,
    val pincode: String? = null,
    val city: String? = null,
    val state: String? = null,
    val address: String? = null,
    val locality: String? = null,
    val addressType: String? = null,
    
    @get:PropertyName("default")
    @set:PropertyName("default")
    var default: Boolean = false
)

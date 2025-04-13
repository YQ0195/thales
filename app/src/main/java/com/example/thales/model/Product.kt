package com.example.thales.model

data class Product(
    val id: Int = 0,
    val name: String,
    val type: String,
    val price: Double,
    val description: String,
    val picture_url: String
)

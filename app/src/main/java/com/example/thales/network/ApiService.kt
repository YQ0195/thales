package com.example.thales.network

import com.example.thales.model.Product
import retrofit2.http.*

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<Product>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Product

    @POST("products")
    suspend fun createProduct(@Body product: Product): Product

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: Product): Unit

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Unit
}

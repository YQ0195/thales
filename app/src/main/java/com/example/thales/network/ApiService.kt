package com.example.thales.network

import com.example.thales.model.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("sortBy") sortBy: String,
        @Query("sortOrder") sortOrder: String,
        @Query("search") search: String,
        @Query("type") type: String? = null
    ): List<Product>

    @GET("products/{id}/no-image")
    suspend fun getProduct(@Path("id") id: Int): Product

    @POST("products")
    suspend fun createProduct(@Body product: Product): Product

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: Product): Unit

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Unit

    @Multipart
    @POST("products")
    suspend fun createProduct(
        @Part("name") name: RequestBody,
        @Part("type") type: RequestBody,
        @Part("price") price: RequestBody,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<Unit>

    @Multipart
    @PUT("products/{id}/with-image")
    suspend fun updateProductWithImage(
        @Path("id") id: Int,
        @Part("name") name: RequestBody,
        @Part("type") type: RequestBody,
        @Part("price") price: RequestBody,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<Unit>

}

package com.example.thales.network

import com.example.thales.model.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

object ProductRepository {

    suspend fun getAllProducts(
        page: Int,
        limit: Int,
        sortBy: String = "price",
        sortOrder: String = "asc",
        search: String = "",
        type: String? = null
    ): List<Product> {
        return RetrofitClient.api.getProducts(
            page = page,
            limit = limit,
            sortBy = sortBy,
            sortOrder = sortOrder,
            search = search,
            type = type
        )
    }

    suspend fun getProductById(id: Int): Product {
        return RetrofitClient.api.getProduct(id)
    }

    suspend fun createProduct(product: Product): Product {
        return RetrofitClient.api.createProduct(product)
    }

    suspend fun updateProduct(id: Int, product: Product) {
        return RetrofitClient.api.updateProduct(id, product)
    }

    suspend fun updateProductMultipart(
        id: Int,
        name: RequestBody,
        type: RequestBody,
        price: RequestBody,
        description: RequestBody,
        image: MultipartBody.Part
    ): Response<Unit> {
        return RetrofitClient.api.updateProductWithImage(id, name, type, price, description, image)
    }

    suspend fun deleteProduct(id: Int) {
        return RetrofitClient.api.deleteProduct(id)
    }

    suspend fun createProductMultipart(
        name: RequestBody,
        type: RequestBody,
        price: RequestBody,
        description: RequestBody,
        image: MultipartBody.Part
    ) {
        RetrofitClient.api.createProduct(name, type, price, description, image)
    }
}

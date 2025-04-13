package com.example.thales.network

import com.example.thales.model.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody

object ProductRepository {

    suspend fun getAllProducts(): List<Product> {
        return RetrofitClient.api.getProducts()
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

package com.example.thales.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thales.model.Product
import com.example.thales.network.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProductViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun fetchProducts() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _products.value = ProductRepository.getAllProducts()
            } catch (e: Exception) {
                // handle error e.g., show Toast/snackbar later
                _products.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    fun createProductMultipart(
        name: RequestBody,
        type: RequestBody,
        price: RequestBody,
        description: RequestBody,
        image: MultipartBody.Part
    ) {
        viewModelScope.launch {
            try {
                ProductRepository.createProductMultipart(name, type, price, description, image)
                fetchProducts() // Optional: refresh product list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}

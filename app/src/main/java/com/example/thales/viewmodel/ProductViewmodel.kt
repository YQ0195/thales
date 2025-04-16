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

    val searchQuery = MutableStateFlow("")
    val sortOrder = MutableStateFlow("asc")
    val currentPage = MutableStateFlow(1)
    val selectedType = MutableStateFlow("")

    var productToEdit: Product? = null

    fun fetchProducts() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _products.value = ProductRepository.getAllProducts(
                    page = currentPage.value,
                    limit = 6,
                    sortBy = "price",
                    sortOrder = sortOrder.value,
                    search = searchQuery.value,
                    type = selectedType.value.takeIf { it.isNotBlank() && it != "All" }
                )
            } catch (e: Exception) {
                _products.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    fun nextPage() {
        currentPage.value++
        fetchProducts()
    }

    fun previousPage() {
        if (currentPage.value > 1) {
            currentPage.value--
            fetchProducts()
        }
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
        currentPage.value = 1
        fetchProducts()
    }

    fun toggleSortOrder() {
        sortOrder.value = if (sortOrder.value == "asc") "desc" else "asc"
        fetchProducts()
    }

    fun setSelectedType(type: String) {
        selectedType.value = type
        currentPage.value = 1
        fetchProducts()
    }

    fun clearFilters() {
        searchQuery.value = ""
        sortOrder.value = "asc"
        selectedType.value = ""
        currentPage.value = 1
        fetchProducts()
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
                fetchProducts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateProduct(id: Int, updatedProduct: Product) {
        viewModelScope.launch {
            try {
                ProductRepository.updateProduct(id, updatedProduct)
                fetchProducts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun updateProductWithMultipartImage(
        id: Int,
        name: RequestBody,
        type: RequestBody,
        price: RequestBody,
        description: RequestBody,
        image: MultipartBody.Part
    ) {
        viewModelScope.launch {
            try {
                ProductRepository.updateProductMultipart(id, name, type, price, description, image)
                fetchProducts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteProduct(id: Int) {
        viewModelScope.launch {
            try {
                ProductRepository.deleteProduct(id)
                fetchProducts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

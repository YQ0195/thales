package com.example.thales.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.thales.viewmodel.ProductViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProductListScreen(
    modifier: Modifier = Modifier,
    viewModel: ProductViewModel = viewModel()
) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchProducts()
    }

    if (isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = products, key = { it.id }) { product ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(product.name, style = MaterialTheme.typography.titleMedium)
                        Text("Type: ${product.type}")
                        Text("Price: $${product.price}")
                        Text(product.description)
                    }
                }
            }
        }
    }
}


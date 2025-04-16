package com.example.thales.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.thales.viewmodel.ProductViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductListScreen(
    viewModel: ProductViewModel,
    onProductClick: (Int) -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()
    val currentPage by viewModel.currentPage.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()

    var typeMenuExpanded by remember { mutableStateOf(false) }
    val categoryOptions = listOf("All", "Electronics", "Clothing", "Books", "Beauty", "Home", "Toys", "Groceries", "Sports", "Automotive")

    LaunchedEffect(Unit) {
        viewModel.fetchProducts()
    }

    Scaffold(

    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    viewModel.setSearchQuery(it)
                },
                label = { Text("Search by name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { viewModel.toggleSortOrder() }) {
                    Text("Price ${if (sortOrder == "asc") "↑" else "↓"}")
                }

                Box {
                    Button(onClick = { typeMenuExpanded = true }) {
                        Text(selectedType.ifBlank { "Type" })
                    }
                    DropdownMenu(
                        expanded = typeMenuExpanded,
                        onDismissRequest = { typeMenuExpanded = false }
                    ) {
                        categoryOptions.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    viewModel.setSelectedType(type)
                                    typeMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Button(onClick = { viewModel.clearFilters() }) {
                    Text("Clear Filters")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(products, key = { it.id }) { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onProductClick(product.id) },
                            elevation = CardDefaults.cardElevation()
                        ) {
                            Column {
                                AsyncImage(
                                    model = "http://10.0.2.2:8080" + product.picture_url,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,

                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                Text(
                                    text = "$${product.price}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .offset(y = (-32).dp),
                        contentAlignment = Alignment.Center
                    ) {
                        FloatingActionButton(onClick = onCreateClick,
                                             modifier = Modifier.size(72.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Create Product",
                                modifier = Modifier.size(36.dp)
                            )

                        }
                    }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { viewModel.previousPage() },
                        enabled = currentPage > 1,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Previous")
                    }

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Page $currentPage")
                    }

                    Button(
                        onClick = { viewModel.nextPage() },
                        enabled = products.size == 6,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Next")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

            }
        }
    }
}

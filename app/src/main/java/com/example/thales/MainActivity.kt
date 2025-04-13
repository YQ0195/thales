package com.example.thales

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thales.ui.ProductCreationForm
import com.example.thales.ui.ProductDetailScreen
import com.example.thales.ui.ProductListScreen
import com.example.thales.ui.theme.ThalesTheme
import com.example.thales.viewmodel.ProductViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ThalesTheme {
                val viewModel: ProductViewModel = viewModel() // ✅ Scoped once here
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.safeDrawing.asPaddingValues())
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = "productList",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("productList") {
                            ProductListScreen(
                                viewModel = viewModel,
                                onProductClick = { productId ->
                                    navController.navigate("productDetail/$productId")
                                },
                                onCreateClick = {
                                    navController.navigate("createProduct")
                                }
                            )
                        }

                        composable("createProduct") {
                            ProductCreationForm(
                                viewModel = viewModel,
                                onProductCreated = { navController.popBackStack() }
                            )
                        }

                        composable("productDetail/{productId}") { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                            val products by viewModel.products.collectAsStateWithLifecycle()
                            val product = products.find { it.id == productId }

                            Log.d("ayo", "productId = $productId, products size = ${products.size}")
                            Log.d("ayo2", products.toString())

                            when {
                                product != null -> ProductDetailScreen(product)
                                products.isEmpty() -> Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                                else -> Text("Product not found")
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.thales

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.thales.ui.ProductListScreen
import com.example.thales.ui.theme.ThalesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThalesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ProductListScreen(modifier = Modifier.padding(innerPadding))
                }

            }
        }
    }
}

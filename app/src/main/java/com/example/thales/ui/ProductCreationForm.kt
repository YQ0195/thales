package com.example.thales.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.thales.model.Product
import com.example.thales.util.rememberImagePickerHandler
import com.example.thales.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCreationForm(
    navController: NavController,
    viewModel: ProductViewModel,
    onProductCreated: () -> Unit,
    productToEdit: Product? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val picker = rememberImagePickerHandler()
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf(productToEdit?.name ?: "") }
    var type by remember { mutableStateOf(productToEdit?.type ?: "") }
    var price by remember { mutableStateOf(productToEdit?.price?.toString() ?: "") }
    var description by remember { mutableStateOf(productToEdit?.description ?: "") }

    var nameError by remember { mutableStateOf(false) }
    var typeError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }
    var imageError by remember { mutableStateOf(false) }

    var showImagePickerDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        picker.onGalleryResult(it)
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        picker.onCameraResult(it)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .imePadding() // to avoid keyboard covering content
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (productToEdit != null) "Edit Product" else "Create Product",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(12.dp))
                .clickable { showImagePickerDialog = true },
            contentAlignment = Alignment.Center
        ) {
            if (picker.imageUri == null && productToEdit?.picture_url == null) {
                Text(
                    "Tap to select image",
                    color = if (imageError) Color.Red else Color.DarkGray
                )
            } else {
                AsyncImage(
                    model = picker.imageUri ?: ("http://10.0.2.2:8080" + productToEdit?.picture_url),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        if (showImagePickerDialog) {
            AlertDialog(
                onDismissRequest = { showImagePickerDialog = false },
                confirmButton = {},
                title = { Text("Select Image") },
                text = {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            showImagePickerDialog = false
                            galleryLauncher.launch("image/*")
                        }) {
                            Icon(Icons.Default.Image, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Gallery")
                        }
                        Button(onClick = {
                            showImagePickerDialog = false
                            val uri = picker.createCameraUri()
                            cameraLauncher.launch(uri)
                        }) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Camera")
                        }
                    }
                }
            )
        }

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = false
            },
            isError = nameError,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        var expanded by remember { mutableStateOf(false) }
        val categoryOptions = listOf("Electronics", "Clothing", "Books", "Beauty", "Appliances", "Toys", "Groceries", "Sports", "Furnitures")

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = type,
                onValueChange = {},
                readOnly = true,
                isError = typeError,
                label = { Text("Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                categoryOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            type = option
                            expanded = false
                            typeError = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = price,
            onValueChange = {
                price = it
                priceError = false
            },
            isError = priceError,
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                descriptionError = false
            },
            isError = descriptionError,
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val validName = name.isNotBlank()
                val validType = type.isNotBlank()
                val validPrice = price.toDoubleOrNull() != null
                val validDesc = description.isNotBlank()
                val validImage = picker.imageUri != null || productToEdit != null

                nameError = !validName
                typeError = !validType
                priceError = !validPrice
                descriptionError = !validDesc
                imageError = !validImage

                if (validName && validType && validPrice && validDesc && validImage) {
                    coroutineScope.launch {
                        if (productToEdit != null && picker.imageUri == null) {
                            val updatedProduct = productToEdit.copy(
                                name = name,
                                type = type,
                                price = price.toDouble(),
                                description = description
                            )
                            viewModel.updateProduct(productToEdit.id, updatedProduct)
                        } else {
                            val file = picker.uriToTempFile() ?: return@launch
                            val imagePart = MultipartBody.Part.createFormData(
                                "image", file.name, file.asRequestBody("image/*".toMediaTypeOrNull())
                            )
                            val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
                            val typePart = type.toRequestBody("text/plain".toMediaTypeOrNull())
                            val pricePart = price.toRequestBody("text/plain".toMediaTypeOrNull())
                            val descPart = description.toRequestBody("text/plain".toMediaTypeOrNull())

                            if (productToEdit != null) {
                                viewModel.updateProductWithMultipartImage(
                                    productToEdit.id, namePart, typePart, pricePart, descPart, imagePart
                                )
                            } else {
                                viewModel.createProductMultipart(namePart, typePart, pricePart, descPart, imagePart)
                            }
                        }
                        onProductCreated()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (productToEdit != null) "Update Product" else "Create Product")
        }
    }
}

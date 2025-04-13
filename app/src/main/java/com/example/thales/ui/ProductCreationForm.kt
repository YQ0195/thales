package com.example.thales.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.thales.util.rememberImagePickerHandler
import com.example.thales.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@Composable
fun ProductCreationForm(
    viewModel: ProductViewModel,
    onProductCreated: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val picker = rememberImagePickerHandler()

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

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

    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            confirmButton = {},
            title = {
                Text("Select Image", style = MaterialTheme.typography.titleMedium)
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            showImagePickerDialog = false
                            galleryLauncher.launch("image/*")
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Gallery")
                    }

                    Button(
                        onClick = {
                            showImagePickerDialog = false
                            val uri = picker.createCameraUri()
                            cameraLauncher.launch(uri)
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Camera")
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(12.dp))
                .clickable { showImagePickerDialog = true },
            contentAlignment = Alignment.Center
        ) {
            if (picker.imageUri == null) {
                Text(
                    "Tap to select image",
                    color = if (imageError) Color.Red else Color.DarkGray
                )
            } else {
                AsyncImage(
                    model = picker.imageUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
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
        OutlinedTextField(
            value = type,
            onValueChange = {
                type = it
                typeError = false
            },
            isError = typeError,
            label = { Text("Type") },
            modifier = Modifier.fillMaxWidth()
        )
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
                // Validate fields
                val validName = name.isNotBlank()
                val validType = type.isNotBlank()
                val validPrice = price.toDoubleOrNull() != null
                val validDesc = description.isNotBlank()
                val validImage = picker.imageUri != null

                nameError = !validName
                typeError = !validType
                priceError = !validPrice
                descriptionError = !validDesc
                imageError = !validImage

                if (validName && validType && validPrice && validDesc && validImage) {
                    coroutineScope.launch {
                        val file = picker.uriToTempFile() ?: return@launch

                        val imagePart = MultipartBody.Part.createFormData(
                            "image",
                            file.name,
                            file.asRequestBody("image/*".toMediaTypeOrNull())
                        )

                        val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
                        val typePart = type.toRequestBody("text/plain".toMediaTypeOrNull())
                        val pricePart = price.toRequestBody("text/plain".toMediaTypeOrNull())
                        val descPart = description.toRequestBody("text/plain".toMediaTypeOrNull())

                        viewModel.createProductMultipart(
                            namePart, typePart, pricePart, descPart, imagePart
                        )

                        onProductCreated()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Create Product")
        }
    }
}

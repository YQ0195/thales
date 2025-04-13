// ImagePickerUtil.kt
package com.example.thales.util

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

class ImagePickerHandler(private val context: Context) {
    var imageUri by mutableStateOf<Uri?>(null)
        private set

    private var _cameraImageFile: File? = null

    fun createCameraUri(): Uri {
        val file = File.createTempFile("product_photo_", ".jpg", context.cacheDir)
        _cameraImageFile = file
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    fun onCameraResult(success: Boolean) {
        if (success) {
            imageUri = _cameraImageFile?.let { file ->
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            }
        }
    }

    fun onGalleryResult(uri: Uri?) {
        imageUri = uri
    }

    fun uriToTempFile(): File? {
        val uri = imageUri ?: return null
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return tempFile
    }
}

@Composable
fun rememberImagePickerHandler(): ImagePickerHandler {
    val context = LocalContext.current
    return remember { ImagePickerHandler(context) }
}
package com.example.thales

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.thales.model.Product
import com.example.thales.viewmodel.ProductViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.DecimalFormat

class ProductCreationActivity : AppCompatActivity() {

    private lateinit var viewModel: ProductViewModel
    private var imageUri: Uri? = null
    private var productToEdit: Product? = null

    private lateinit var nameInput: EditText
    private lateinit var typeSpinner: Spinner
    private lateinit var priceInput: EditText
    private lateinit var descInput: EditText
    private lateinit var imagePreview: ImageView
    private lateinit var submitBtn: Button

    private val imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            imagePreview.setImageURI(imageUri)
        } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(result.data), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_creation)

        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        nameInput = findViewById(R.id.name_input)
        typeSpinner = findViewById(R.id.type_spinner)
        priceInput = findViewById(R.id.price_input)
        descInput = findViewById(R.id.description_input)
        imagePreview = findViewById(R.id.image_preview)
        submitBtn = findViewById(R.id.submit_button)

        val typeOptions = resources.getStringArray(R.array.product_types)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typeOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = spinnerAdapter

        // Load product if editing
        productToEdit = intent.getSerializableExtra(ProductDetailActivity.EXTRA_PRODUCT) as? Product
        productToEdit?.let { product ->
            nameInput.setText(product.name)
            typeSpinner.setSelection(typeOptions.indexOf(product.type))
            priceInput.setText(DecimalFormat("0.00").format(product.price))
            descInput.setText(product.description.replace("\\n", "\n"))
            imagePreview.load("http://10.0.2.2:8080${product.picture_url}")
        }

        imagePreview.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent { intent -> imageLauncher.launch(intent) }
        }

        submitBtn.setOnClickListener {
            val name = nameInput.text.toString()
            val type = typeSpinner.selectedItem.toString()
            val price = priceInput.text.toString().toDoubleOrNull()
            val description = descInput.text.toString()

            if (name.isBlank() || type.isBlank() || price == null || description.isBlank()) {
                Toast.makeText(this, "Please fill all fields correctly.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val typePart = type.toRequestBody("text/plain".toMediaTypeOrNull())
            val pricePart = String.format("%.2f", price).toRequestBody("text/plain".toMediaTypeOrNull())
            val descPart = description.toRequestBody("text/plain".toMediaTypeOrNull())

            val imageFile = imageUri?.let { uri ->
                val inputStream = contentResolver.openInputStream(uri)
                val tempFile = File.createTempFile("upload", ".jpg", cacheDir)
                inputStream?.use { it.copyTo(tempFile.outputStream()) }
                tempFile
            }

            val imagePart = imageFile?.let {
                MultipartBody.Part.createFormData(
                    "image", it.name, it.asRequestBody("image/*".toMediaTypeOrNull())
                )
            }

            if (productToEdit != null) {
                if (imagePart != null) {
                    viewModel.updateProductWithMultipartImage(
                        productToEdit!!.id, namePart, typePart, pricePart, descPart, imagePart
                    )
                } else {
                    val updated = productToEdit!!.copy(name = name, type = type, price = price, description = description)
                    viewModel.updateProduct(productToEdit!!.id, updated)
                }
            } else {
                if (imagePart == null) {
                    Toast.makeText(this, "Image is required", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                viewModel.createProductMultipart(namePart, typePart, pricePart, descPart, imagePart)
            }

            // Always go back to ProductListActivity
            val intent = Intent(this, ProductListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}

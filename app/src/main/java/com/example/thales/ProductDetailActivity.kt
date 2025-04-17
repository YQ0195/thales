package com.example.thales

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.thales.model.Product
import com.example.thales.viewmodel.ProductViewModel

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var viewModel: ProductViewModel

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }

    private lateinit var currentProduct: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        val incomingIntent = intent
        val product = incomingIntent.getSerializableExtra(EXTRA_PRODUCT) as? Product

        if (product == null) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        currentProduct = product

        val backButton = findViewById<ImageButton>(R.id.back_button)
        val moreButton = findViewById<ImageButton>(R.id.more_button)
        val imageView = findViewById<ImageView>(R.id.product_image)
        val nameText = findViewById<TextView>(R.id.product_name)
        val typeText = findViewById<TextView>(R.id.product_type)
        val priceText = findViewById<TextView>(R.id.product_price)
        val descriptionText = findViewById<TextView>(R.id.product_description)

        fun displayProduct(p: Product) {
            nameText.text = p.name
            typeText.text = "Type: ${p.type}"
            priceText.text = "$%.2f".format(p.price)
            descriptionText.text = "Description:\n" + p.description.replace("\\n", "\n")
            imageView.load("http://10.0.2.2:8080${p.picture_url}")
        }

        displayProduct(product)

        backButton.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        moreButton.setOnClickListener {
            val popupMenu = PopupMenu(this, moreButton)
            popupMenu.menuInflater.inflate(R.menu.menu_product_options, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        val editIntent = Intent(this, ProductCreationActivity::class.java)
                        editIntent.putExtra(EXTRA_PRODUCT, currentProduct)
                        startActivityForResult(editIntent, 100)
                        true
                    }
                    R.id.action_delete -> {
                        AlertDialog.Builder(this)
                            .setTitle("Delete Product")
                            .setMessage("Are you sure you want to delete ${currentProduct.name}?")
                            .setPositiveButton("Delete") { _, _ ->
                                viewModel.deleteProduct(product.id)

                                val resultIntent = Intent().apply {
                                    putExtra(EXTRA_PRODUCT, currentProduct)
                                }
                                setResult(Activity.RESULT_OK, resultIntent)
                                finish()
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish() // This will tell the ProductList to refresh when returning
        }
    }
}

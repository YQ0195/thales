package com.example.thales

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thales.model.Product
import com.example.thales.viewmodel.ProductViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductListActivity : AppCompatActivity() {

    private val viewModel: ProductViewModel by viewModels()
    private lateinit var adapter: ProductAdapter
    private lateinit var pageText: TextView

    private val refreshLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        viewModel.fetchProducts()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        val searchInput = findViewById<EditText>(R.id.search_input)
        val sortButton = findViewById<Button>(R.id.sort_button)
        val spinner = findViewById<Spinner>(R.id.type_spinner)
        val recyclerView = findViewById<RecyclerView>(R.id.product_recycler)
        val clearFilters = findViewById<Button>(R.id.clear_filter_button)
        val prevButton = findViewById<Button>(R.id.previous_button)
        val nextButton = findViewById<Button>(R.id.next_button)
        val fab = findViewById<ImageButton>(R.id.fab_add_product)
        pageText = findViewById(R.id.page_text)

        adapter = ProductAdapter { product ->
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product)
            refreshLauncher.launch(intent)
        }

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        val categories = listOf("All", "Electronics", "Clothing", "Books", "Beauty", "Appliances", "Toys", "Groceries", "Sports", "Furnitures")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                viewModel.setSelectedType(categories[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        lifecycleScope.launch {
            viewModel.products.collectLatest {
                adapter.submitList(it)
            }
        }

        searchInput.addTextChangedListener {
            viewModel.setSearchQuery(it.toString())
        }

        sortButton.setOnClickListener {
            viewModel.toggleSortOrder()
            sortButton.text = "Price ${if (viewModel.sortOrder.value == "asc") "↑" else "↓"}"
        }

        clearFilters.setOnClickListener {
            viewModel.clearFilters()
            searchInput.setText("")
            spinner.setSelection(0)
        }

        prevButton.setOnClickListener { viewModel.previousPage() }
        nextButton.setOnClickListener { viewModel.nextPage() }

        lifecycleScope.launch {
            viewModel.currentPage.collectLatest {
                pageText.text = "Page $it"
            }
        }

        fab.setOnClickListener {
            val intent = Intent(this, ProductCreationActivity::class.java)
            refreshLauncher.launch(intent)
        }
    }
}

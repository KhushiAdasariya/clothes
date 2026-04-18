package com.example.admin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.databinding.ActivityAddProductBinding
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.io.InputStream

class add_product : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private var imageUri: Uri? = null
    private var encodedImage: String? = null
    private val db = FirebaseDatabase.getInstance().getReference("Products")
    
    private var isEditMode = false
    private var existingProductId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        setupCategoryDropdown()
        checkEditMode()

        // Select Image
        binding.cardSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            galleryLauncher.launch(intent)
        }

        // Upload/Update Product
        binding.btnAddProduct.setOnClickListener {
            validateAndUpload()
        }
    }

    private fun setupCategoryDropdown() {
        // Updated to only 3 categories
        val categories = arrayOf("Men", "Women", "Kids")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.autoCompleteCategory.setAdapter(adapter)
    }

    private fun checkEditMode() {
        isEditMode = intent.getBooleanExtra("IS_EDIT", false)
        if (isEditMode) {
            existingProductId = intent.getStringExtra("PRODUCT_ID")
            val name = intent.getStringExtra("PRODUCT_NAME")
            val price = intent.getStringExtra("PRODUCT_PRICE")
            val category = intent.getStringExtra("PRODUCT_CATEGORY")
            val description = intent.getStringExtra("PRODUCT_DESCRIPTION")
            encodedImage = intent.getStringExtra("PRODUCT_IMAGE")

            binding.etProductName.setText(name)
            binding.etProductPrice.setText(price)
            binding.autoCompleteCategory.setText(category, false)
            binding.etProductDescription.setText(description)
            binding.btnAddProduct.text = "Update Product"
            binding.layoutPlaceholder.visibility = View.GONE

            if (!encodedImage.isNullOrEmpty()) {
                try {
                    val imageBytes = Base64.decode(encodedImage, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    binding.imgProduct.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            if (imageUri != null) {
                binding.imgProduct.setImageURI(imageUri)
                binding.layoutPlaceholder.visibility = View.GONE
                encodedImage = encodeImage(imageUri!!)
            }
        }
    }

    private fun encodeImage(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos)
            val imageBytes = baos.toByteArray()
            Base64.encodeToString(imageBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }

    private fun validateAndUpload() {
        val name = binding.etProductName.text.toString().trim()
        val price = binding.etProductPrice.text.toString().trim()
        val category = binding.autoCompleteCategory.text.toString().trim()
        val description = binding.etProductDescription.text.toString().trim()

        if (encodedImage == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }
        if (name.isEmpty() || price.isEmpty() || category.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnAddProduct.isEnabled = false

        val productId = if (isEditMode) existingProductId!! else db.push().key ?: ""
        
        val productMap = hashMapOf(
            "id" to productId,
            "name" to name,
            "price" to price,
            "category" to category,
            "description" to description,
            "image" to encodedImage
        )

        db.child(productId).setValue(productMap)
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                binding.btnAddProduct.isEnabled = true
                val msg = if (isEditMode) "Product Updated!" else "Product Uploaded!"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                finish() 
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                binding.btnAddProduct.isEnabled = true
                Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

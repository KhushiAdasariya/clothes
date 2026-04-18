package com.example.clothy

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.clothy.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.io.InputStream

class EditProfile : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private var imageUri: Uri? = null
    private var encodedImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        // 1. Auto-fill data on page load
        loadUserData()

        binding.btnBack.setOnClickListener { finish() }

        binding.tvChangePicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            galleryLauncher.launch(intent)
        }

        binding.btnSave.setOnClickListener {
            saveProfileData()
        }
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid

        binding.progressBar.visibility = View.VISIBLE
        
        // Fixed Email from Firebase Auth
        binding.etEmail.setText(currentUser.email)

        // Fetching data from Realtime Database
        db.getReference("Users").child(uid).get()
            .addOnSuccessListener { snapshot ->
                binding.progressBar.visibility = View.GONE
                if (snapshot.exists()) {
                    // Auto-filling fields
                    binding.etFullName.setText(snapshot.child("name").value?.toString() ?: "")
                    binding.etPhone.setText(snapshot.child("mobile").value?.toString() ?: "")
                    binding.etGender.setText(snapshot.child("gender").value?.toString() ?: "")
                    binding.etOccupation.setText(snapshot.child("occupation").value?.toString() ?: "")
                    binding.etAddress.setText(snapshot.child("address").value?.toString() ?: "")
                    binding.etCity.setText(snapshot.child("city").value?.toString() ?: "")
                    binding.etState.setText(snapshot.child("state").value?.toString() ?: "")
                    binding.etPincode.setText(snapshot.child("pincode").value?.toString() ?: "")

                    // Load Profile Image if exists
                    val base64Image = snapshot.child("profileImage").value?.toString()
                    if (!base64Image.isNullOrEmpty()) {
                        try {
                            val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            binding.imgUser.setImageBitmap(decodedImage)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Failed to load profile data", Toast.LENGTH_SHORT).show()
            }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            if (imageUri != null) {
                binding.imgUser.setImageURI(imageUri)
                encodedImage = encodeImage(imageUri!!)
            }
        }
    }

    private fun encodeImage(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            val imageBytes = baos.toByteArray()
            Base64.encodeToString(imageBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }

    private fun saveProfileData() {
        val uid = auth.currentUser?.uid ?: return
        
        // Getting data from EditTexts
        val name = binding.etFullName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val gender = binding.etGender.text.toString().trim()
        val occupation = binding.etOccupation.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val city = binding.etCity.text.toString().trim()
        val state = binding.etState.text.toString().trim()
        val pincode = binding.etPincode.text.toString().trim()

        // Validations
        if (name.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (phone.length != 10) {
            Toast.makeText(this, "Please enter a valid 10-digit number", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnSave.isEnabled = false

        val userMap = mutableMapOf<String, Any>(
            "name" to name,
            "mobile" to phone,
            "gender" to gender,
            "occupation" to occupation,
            "address" to address,
            "city" to city,
            "state" to state,
            "pincode" to pincode
        )
        
        if (encodedImage != null) {
            userMap["profileImage"] = encodedImage!!
        }

        // Updating Realtime Database
        db.getReference("Users").child(uid).updateChildren(userMap)
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                binding.btnSave.isEnabled = true
                Toast.makeText(this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                binding.btnSave.isEnabled = true
                Toast.makeText(this, "Update Failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }
}

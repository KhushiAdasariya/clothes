package com.example.clothy

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.clothy.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Default fragment
        val navigateTo = intent.getStringExtra("NAVIGATE_TO")
        if (navigateTo == "order") {
            replaceFragment(Order())
            binding.bottomNavigationView.selectedItemId = R.id.order
        } else {
            replaceFragment(Home())
            binding.bottomNavigationView.selectedItemId = R.id.home
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(Home())
                R.id.contact -> replaceFragment(Contact())
                R.id.order -> replaceFragment(Order())
                R.id.profile -> replaceFragment(Profile())
            }
            true
        }

        // Handle Back Press Logic
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currentFragment !is Home) {
                    // If not on Home, go to Home first
                    binding.bottomNavigationView.selectedItemId = R.id.home
                    replaceFragment(Home())
                } else {
                    // If already on Home, show Exit Confirmation Dialog
                    showExitDialog()
                }
            }
        })
    }

    private fun replaceFragment(fragment: Fragment){
        currentFragment = fragment
        val fragmentManager = supportFragmentManager
        val fragmentTransition = fragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.frame_layout, fragment)
        fragmentTransition.commit()
    }

    private fun showExitDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit Clothify?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                finish() // Close the app
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}

package com.example.clothy

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Profile : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var profileImage: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        // Profile Info UI
        tvName = view.findViewById(R.id.tvProfileName)
        tvEmail = view.findViewById(R.id.tvProfileEmail)
        profileImage = view.findViewById(R.id.profileImage)

        val btnEditSettings = view.findViewById<TextView>(R.id.btneditprofile)

        // Menu buttons
        val btnOrders = view.findViewById<TextView>(R.id.btnOrders)
        val btnWishlist = view.findViewById<TextView>(R.id.btnWishlist)
        val btnCart = view.findViewById<TextView>(R.id.btnCart)
        val btnAddress = view.findViewById<TextView>(R.id.btnAddress)
        val btnPayments = view.findViewById<TextView>(R.id.btnPayments)
        val btnPrivacy = view.findViewById<TextView>(R.id.btnPrivacy)
        val btnHelp = view.findViewById<TextView>(R.id.btnHelp)
        val btnLogout = view.findViewById<TextView>(R.id.btnLogout)

        // Real-time listener to load and sync user data
        val currentUser = auth.currentUser
        if (currentUser != null) {
            tvEmail.text = currentUser.email ?: ""
            
            val userRef = db.getReference("Users").child(currentUser.uid)
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (isAdded && snapshot.exists()) {
                        val name = snapshot.child("name").value?.toString()
                        tvName.text = name ?: "User Name"

                        val base64Image = snapshot.child("profileImage").value?.toString()
                        if (!base64Image.isNullOrEmpty()) {
                            try {
                                val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                profileImage.setImageBitmap(decodedImage)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        // --- BUTTON CLICK LISTENERS ---

        btnEditSettings.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfile::class.java))
        }

        btnOrders.setOnClickListener {
            replaceFragment(Order())
        }

        btnWishlist.setOnClickListener {
            startActivity(Intent(requireContext(), WishlistActivity::class.java))
        }

        btnCart.setOnClickListener {
            startActivity(Intent(requireContext(), CartActivity::class.java))
        }

        btnAddress.setOnClickListener {
            startActivity(Intent(requireContext(), Manage_Address::class.java))
        }

        btnPayments.setOnClickListener {
            startActivity(Intent(requireContext(), payment::class.java))
        }
        
        btnPrivacy.setOnClickListener {
            // Future implementation
        }

        btnHelp.setOnClickListener {
            startActivity(Intent(requireContext(), HelpActivity::class.java))
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), Login::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        return view
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransition = fragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.frame_layout, fragment)
        fragmentTransition.addToBackStack(null)
        fragmentTransition.commit()
    }
}

package com.example.clothy

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.content.Intent

class Home : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val tvAll = view.findViewById<TextView>(R.id.tvAll)
        val tvWomen = view.findViewById<TextView>(R.id.tvWomen)
        val tvMen = view.findViewById<TextView>(R.id.tvMen)
        val tvKids = view.findViewById<TextView>(R.id.tvKids)

        val btnSearch = view.findViewById<ImageView>(R.id.btnSearch)
        val btnCart = view.findViewById<ImageView>(R.id.btnCart)
        val btnWishlist = view.findViewById<ImageView>(R.id.btnWishlist)

        // default fragment
        loadFragment(AllFragment())

        fun resetTabs() {
            tvAll.background = null
            tvWomen.background = null
            tvMen.background = null
            tvKids.background = null

            tvAll.setTextColor(Color.BLACK)
            tvWomen.setTextColor(Color.BLACK)
            tvMen.setTextColor(Color.BLACK)
            tvKids.setTextColor(Color.BLACK)
        }

        tvAll.setOnClickListener {
            resetTabs()
            tvAll.setBackgroundResource(R.drawable.tab_selected_bg)
            tvAll.setTextColor(Color.WHITE)
            loadFragment(AllFragment())
        }

        tvWomen.setOnClickListener {
            resetTabs()
            tvWomen.setBackgroundResource(R.drawable.tab_selected_bg)
            tvWomen.setTextColor(Color.WHITE)
            loadFragment(WomenFragment())
        }

        tvMen.setOnClickListener {
            resetTabs()
            tvMen.setBackgroundResource(R.drawable.tab_selected_bg)
            tvMen.setTextColor(Color.WHITE)
            loadFragment(MenFragment())
        }

        tvKids.setOnClickListener {
            resetTabs()
            tvKids.setBackgroundResource(R.drawable.tab_selected_bg)
            tvKids.setTextColor(Color.WHITE)
            loadFragment(KidsFragment())
        }

        btnSearch.setOnClickListener {
            startActivity(Intent(requireContext(), Searchactivity::class.java))
        }

        btnCart.setOnClickListener {
            startActivity(Intent(requireContext(), CartActivity::class.java))
        }

        btnWishlist.setOnClickListener {
            startActivity(Intent(requireContext(), WishlistActivity::class.java))
        }

        return view
    }

    private fun loadFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}

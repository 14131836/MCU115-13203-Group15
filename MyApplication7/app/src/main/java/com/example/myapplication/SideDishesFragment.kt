package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class SideDishesFragment : Fragment(R.layout.fragment_side_dishes) {
    private lateinit var viewModel: OrderViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[OrderViewModel::class.java]

        val cbFries = view.findViewById<CheckBox>(R.id.cbFries)
        val cbSalad = view.findViewById<CheckBox>(R.id.cbSalad)
        val cbSoup = view.findViewById<CheckBox>(R.id.cbSoup)
        val btnDone = view.findViewById<Button>(R.id.btnDone)

        viewModel.order.value?.sideDishes?.let { sides ->
            cbFries.isChecked = sides.contains("Fries")
            cbSalad.isChecked = sides.contains("Salad")
            cbSoup.isChecked = sides.contains("Onion Soup")
        }

        btnDone.setOnClickListener {
            val sides = mutableListOf<String>()
            if (cbFries.isChecked) sides.add("Fries")
            if (cbSalad.isChecked) sides.add("Salad")
            if (cbSoup.isChecked) sides.add("Onion Soup")

            viewModel.setSideDishes(sides)
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}

package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class MainMealFragment : Fragment(R.layout.fragment_main_meal) {
    private lateinit var viewModel: OrderViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[OrderViewModel::class.java]

        val rgMainMeal = view.findViewById<RadioGroup>(R.id.rgMainMeal)
        val btnDone = view.findViewById<Button>(R.id.btnDone)

        viewModel.order.value?.mainMeal?.let { meal ->
            when (meal) {
                "Burger" -> rgMainMeal.check(R.id.rbBurger)
                "Fried Chicken" -> rgMainMeal.check(R.id.rbFriedChicken)
                "Chicken Nuggets" -> rgMainMeal.check(R.id.rbChickenNuggets)
            }
        }

        btnDone.setOnClickListener {
            val selectedId = rgMainMeal.checkedRadioButtonId
            val meal = when (selectedId) {
                R.id.rbBurger -> "Burger"
                R.id.rbFriedChicken -> "Fried Chicken"
                R.id.rbChickenNuggets -> "Chicken Nuggets"
                else -> null
            }
            if (meal != null) {
                viewModel.setMainMeal(meal)
            }
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}

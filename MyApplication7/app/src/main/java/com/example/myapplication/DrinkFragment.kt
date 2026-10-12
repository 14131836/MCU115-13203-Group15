package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class DrinkFragment : Fragment(R.layout.fragment_drink) {
    private lateinit var viewModel: OrderViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[OrderViewModel::class.java]

        val rgDrink = view.findViewById<RadioGroup>(R.id.rgDrink)
        val btnDone = view.findViewById<Button>(R.id.btnDone)

        viewModel.order.value?.drink?.let { drink ->
            when (drink) {
                "Cola" -> rgDrink.check(R.id.rbCola)
                "Iced Tea" -> rgDrink.check(R.id.rbIcedTea)
                "Water" -> rgDrink.check(R.id.rbWater)
            }
        }

        btnDone.setOnClickListener {
            val selectedId = rgDrink.checkedRadioButtonId
            val drink = when (selectedId) {
                R.id.rbCola -> "Cola"
                R.id.rbIcedTea -> "Iced Tea"
                R.id.rbWater -> "Water"
                else -> null
            }
            if (drink != null) {
                viewModel.setDrink(drink)
            }
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}

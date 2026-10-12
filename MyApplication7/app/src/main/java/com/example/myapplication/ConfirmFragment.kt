package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class ConfirmFragment : Fragment(R.layout.fragment_confirm) {
    private lateinit var viewModel: OrderViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[OrderViewModel::class.java]

        val tvOrderSummary = view.findViewById<TextView>(R.id.tvOrderSummary)
        val btnConfirm = view.findViewById<Button>(R.id.btnConfirm)

        viewModel.order.observe(viewLifecycleOwner) { order ->
            tvOrderSummary.text = "Main: ${order.mainMeal ?: "-"}\n" +
                    "Sides: ${if (order.sideDishes.isEmpty()) "-" else order.sideDishes.joinToString(", ")}\n" +
                    "Drink: ${order.drink ?: "-"}"
        }

        btnConfirm.setOnClickListener {
            val order = viewModel.order.value
            if (order?.isComplete() != true) {
                Toast.makeText(
                    requireContext(),
                    "Please select a main meal, at least one side dish, and a drink.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Submit Order")
                .setMessage(
                    "Main: ${order.mainMeal}\n" +
                            "Sides: ${order.sideDishes.joinToString(", ")}\n" +
                            "Drink: ${order.drink}\n\nSubmit this order?"
                )
                .setPositiveButton("Submit") { d, _ ->
                    d.dismiss()
                    Toast.makeText(requireContext(), "Order submitted! 🥳", Toast.LENGTH_SHORT).show()
                    viewModel.reset()
                    requireActivity().finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}

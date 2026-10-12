package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this)[OrderViewModel::class.java]

        val btnChooseMainMeal = findViewById<Button>(R.id.btnChooseMainMeal)
        val btnChooseSideDishes = findViewById<Button>(R.id.btnChooseSideDishes)
        val btnChooseDrink = findViewById<Button>(R.id.btnChooseDrink)
        val btnOrder = findViewById<Button>(R.id.btnOrder)
        val tvSummary = findViewById<TextView>(R.id.tvSummary)

        viewModel.order.observe(this) { order ->
            tvSummary.text = "Current Selections:\n" +
                    "Main: ${order.mainMeal ?: "-"}\n" +
                    "Sides: ${if (order.sideDishes.isEmpty()) "-" else order.sideDishes.joinToString(", ")}\n" +
                    "Drink: ${order.drink ?: "-"}"
        }

        btnChooseMainMeal.setOnClickListener {
            openFragment(MainMealFragment())
        }

        btnChooseSideDishes.setOnClickListener {
            openFragment(SideDishesFragment())
        }

        btnChooseDrink.setOnClickListener {
            openFragment(DrinkFragment())
        }

        btnOrder.setOnClickListener {
            openFragment(ConfirmFragment())
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val container = findViewById<View>(R.id.fragmentContainer)
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                    if (supportFragmentManager.backStackEntryCount <= 1) {
                        container.visibility = View.GONE
                    }
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun openFragment(fragment: Fragment) {
        val container = findViewById<View>(R.id.fragmentContainer)
        container.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}

package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrderViewModel : ViewModel() {
    private val _order = MutableLiveData(Order())
    val order: LiveData<Order> get() = _order

    fun setMainMeal(meal: String) {
        val current = _order.value ?: Order()
        current.mainMeal = meal
        _order.value = current
    }

    fun setSideDishes(sides: MutableList<String>) {
        val current = _order.value ?: Order()
        current.sideDishes = sides
        _order.value = current
    }

    fun setDrink(d: String) {
        val current = _order.value ?: Order()
        current.drink = d
        _order.value = current
    }

    fun reset() {
        _order.value = Order()
    }
}

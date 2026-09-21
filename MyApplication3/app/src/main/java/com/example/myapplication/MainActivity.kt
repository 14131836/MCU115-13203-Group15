package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentInput = ""
    private var fullExpression = ""
    private var lastResult = 0.0
    private var currentOperator: String? = null
    private var isNewInput = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        val numberButtons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9, binding.btnDot
        )

        numberButtons.forEach { button ->
            button.setOnClickListener { appendNumber((it as Button).text.toString()) }
        }

        binding.btnAdd.setOnClickListener { setOperator("+") }
        binding.btnSub.setOnClickListener { setOperator("-") }
        binding.btnMul.setOnClickListener { setOperator("×") }
        binding.btnDiv.setOnClickListener { setOperator("÷") }

        binding.btnEqual.setOnClickListener { calculateResult() }
        binding.btnAc.setOnClickListener { clearAll() }
        binding.btnC.setOnClickListener { clearEntry() }
        binding.btnBackspace.setOnClickListener { deleteLast() }
    }

    private fun appendNumber(number: String) {
        if (isNewInput) {
            currentInput = if (number == ".") "0." else number
            isNewInput = false
        } else {
            if (number == "." && currentInput.contains(".")) return
            currentInput += number
        }
        updateDisplay(currentInput)
    }

    private fun setOperator(operator: String) {
        if (currentInput.isNotEmpty()) {
            if (currentOperator != null) {
                // 如果已經有運算符，先計算前面的結果
                performCalculation()
            } else {
                lastResult = currentInput.toDoubleOrNull() ?: 0.0
            }
            
            fullExpression = "${formatResult(lastResult)} $operator "
            binding.tvExpression.text = fullExpression
            currentOperator = operator
            isNewInput = true
            currentInput = ""
        } else if (fullExpression.isNotEmpty() && currentOperator != null) {
            // 允許在輸入第二個數字前更改運算符
            currentOperator = operator
            fullExpression = fullExpression.dropLast(3) + " $operator "
            binding.tvExpression.text = fullExpression
        }
    }

    private fun performCalculation() {
        val secondOperand = currentInput.toDoubleOrNull() ?: 0.0
        lastResult = when (currentOperator) {
            "+" -> lastResult + secondOperand
            "-" -> lastResult - secondOperand
            "×" -> lastResult * secondOperand
            "÷" -> if (secondOperand != 0.0) lastResult / secondOperand else Double.NaN
            else -> secondOperand
        }
    }

    private fun calculateResult() {
        if (currentOperator == null || currentInput.isEmpty()) return

        val secondOperand = currentInput.toDoubleOrNull() ?: 0.0
        
        // 更新上方顯示完整的運算式
        binding.tvExpression.text = "$fullExpression$currentInput ="
        
        performCalculation()
        
        currentInput = formatResult(lastResult)
        updateDisplay(currentInput)
        
        currentOperator = null
        fullExpression = ""
        isNewInput = true
    }

    private fun formatResult(result: Double): String {
        return if (result.isNaN()) "Error"
        else if (result == result.toLong().toDouble()) {
            result.toLong().toString()
        } else {
            result.toString()
        }
    }

    private fun clearAll() {
        currentInput = ""
        fullExpression = ""
        lastResult = 0.0
        currentOperator = null
        isNewInput = true
        updateDisplay("0")
        binding.tvExpression.text = ""
    }

    private fun clearEntry() {
        currentInput = ""
        isNewInput = true
        updateDisplay("0")
    }

    private fun deleteLast() {
        if (currentInput.isNotEmpty() && currentInput != "Error") {
            currentInput = currentInput.dropLast(1)
            if (currentInput.isEmpty()) {
                updateDisplay("0")
                isNewInput = true
            } else {
                updateDisplay(currentInput)
            }
        }
    }

    private fun updateDisplay(text: String) {
        binding.tvDisplay.text = text
    }
}

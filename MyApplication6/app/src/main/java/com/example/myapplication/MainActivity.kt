package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvReceivedResult: TextView

    // ST 01 MainActivity 宣告 ActivityResultLauncher 作為 Activity 啟動器
    private val secActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // ST 05 MainActivity 使用 ActivityResultLauncher 取得回傳的資料
        if (result.resultCode == RESULT_OK) {
            val returnMessage = result.data?.getStringExtra("EXTRA_RETURN_MESSAGE")
            if (!returnMessage.isNullOrEmpty()) {
                tvReceivedResult.text = returnMessage
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvMessageToSend = findViewById<TextView>(R.id.tvMessageToSend)
        val btnSwitchPage = findViewById<Button>(R.id.btnSwitchPage)
        tvReceivedResult = findViewById<TextView>(R.id.tvReceivedResult)

        btnSwitchPage.setOnClickListener {
            val message = tvMessageToSend.text.toString()
            // ST 02 MainActivity 使用 ActivityResultLauncher 發送資料，並前往 SecActivity
            val intent = Intent(this, SecActivity::class.java).apply {
                putExtra("EXTRA_MESSAGE", message)
            }
            secActivityLauncher.launch(intent)
        }
    }
}

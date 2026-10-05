package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SecActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sec)

        val tvReceivedMessage = findViewById<TextView>(R.id.tvReceivedMessage)
        val etReturnMessage = findViewById<EditText>(R.id.etReturnMessage)
        val btnReturn = findViewById<Button>(R.id.btnReturn)

        // ST 03 接收從 MainActivity 傳過來的資料
        val receivedText = intent.getStringExtra("EXTRA_MESSAGE") ?: ""
        tvReceivedMessage.text = receivedText

        btnReturn.setOnClickListener {
            val returnText = etReturnMessage.text.toString()
            val resultIntent = Intent().apply {
                putExtra("EXTRA_RETURN_MESSAGE", returnText)
            }
            // ST 03 使用 setResult() 儲存要回傳的資料
            setResult(RESULT_OK, resultIntent)
            // ST 04 使用 finish() 結束 SecActivity，並回傳到 MainActivity
            finish()
        }
    }
}

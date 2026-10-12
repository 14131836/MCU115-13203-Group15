package com.example.myapplication

<<<<<<< HEAD
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
=======
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
>>>>>>> 0cb3036 (zzz)

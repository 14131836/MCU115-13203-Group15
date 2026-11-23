package com.example.myapplication

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: MyDBHelper
    private lateinit var db: SQLiteDatabase

    private lateinit var edBrand: EditText
    private lateinit var edModel: EditText
    private lateinit var edYear: EditText
    private lateinit var edPrice: EditText

    private lateinit var btnInsert: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var btnQuery: Button

    private lateinit var listView: ListView
    private val itemList = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 初始化資料庫
        try {
            dbHelper = MyDBHelper(this)
            db = dbHelper.writableDatabase
        } catch (e: Exception) {
            Toast.makeText(this, "資料庫初始化失敗: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }

        // 綁定 UI 元件
        edBrand = findViewById(R.id.ed_brand)
        edModel = findViewById(R.id.ed_model)
        edYear = findViewById(R.id.ed_year)
        edPrice = findViewById(R.id.ed_price)

        btnInsert = findViewById(R.id.btn_insert)
        btnUpdate = findViewById(R.id.btn_update)
        btnDelete = findViewById(R.id.btn_delete)
        btnQuery = findViewById(R.id.btn_query)

        listView = findViewById(R.id.listView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemList)
        listView.adapter = adapter

        // 設定按鈕點擊監聽器
        btnInsert.setOnClickListener { insertCar() }
        btnUpdate.setOnClickListener { updateCar() }
        btnDelete.setOnClickListener { deleteCar() }
        btnQuery.setOnClickListener { queryCar() }

        // 點擊列表項目可自動填入輸入欄位，方便修改與刪除
        listView.setOnItemClickListener { _, _, position, _ ->
            val itemStr = itemList[position]
            val regex = Regex("""廠牌:(.*?)\s+車型:(.*?)\s+年份:(.*?)\s+價格:(.*)""")
            val match = regex.find(itemStr)
            if (match != null) {
                edBrand.setText(match.groupValues[1].trim())
                edModel.setText(match.groupValues[2].trim())
                edYear.setText(match.groupValues[3].trim())
                edPrice.setText(match.groupValues[4].trim())
            }
        }

        // 載入初始測試資料與查詢
        initSampleDataIfNeeded()
        queryCar()
    }

    // 若資料庫為空，插入範例資料（呈現像照片一樣的效果）
    private fun initSampleDataIfNeeded() {
        try {
            val cursor = db.query(MyDBHelper.TABLE_NAME, null, null, null, null, null, null)
            if (cursor.count == 0) {
                val samples = listOf(
                    ContentValues().apply { put(MyDBHelper.COL_BRAND, "Toyota"); put(MyDBHelper.COL_MODEL, "RAV4"); put(MyDBHelper.COL_YEAR, 2020); put(MyDBHelper.COL_PRICE, 65) },
                    ContentValues().apply { put(MyDBHelper.COL_BRAND, "Honda"); put(MyDBHelper.COL_MODEL, "CR-V"); put(MyDBHelper.COL_YEAR, 2021); put(MyDBHelper.COL_PRICE, 75) },
                    ContentValues().apply { put(MyDBHelper.COL_BRAND, "Nissan"); put(MyDBHelper.COL_MODEL, "Kicks"); put(MyDBHelper.COL_YEAR, 2019); put(MyDBHelper.COL_PRICE, 48) },
                    ContentValues().apply { put(MyDBHelper.COL_BRAND, "Mazda"); put(MyDBHelper.COL_MODEL, "CX-5"); put(MyDBHelper.COL_YEAR, 2022); put(MyDBHelper.COL_PRICE, 82) }
                )
                for (cv in samples) {
                    db.insert(MyDBHelper.TABLE_NAME, null, cv)
                }
            }
            cursor.close()
        } catch (_: Exception) {
        }
    }

    // 1. 新增功能 (Insert)
    private fun insertCar() {
        val brand = edBrand.text.toString().trim()
        val model = edModel.text.toString().trim()
        val yearStr = edYear.text.toString().trim()
        val priceStr = edPrice.text.toString().trim()

        if (brand.isEmpty() || model.isEmpty() || yearStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "請填寫所有欄位 (廠牌、車型、年份、價格皆為必填)！", Toast.LENGTH_SHORT).show()
            return
        }

        val year = yearStr.toIntOrNull()
        val price = priceStr.toIntOrNull()

        if (year == null || price == null) {
            Toast.makeText(this, "年份與價格必須為有效數字！", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val values = ContentValues().apply {
                put(MyDBHelper.COL_BRAND, brand)
                put(MyDBHelper.COL_MODEL, model)
                put(MyDBHelper.COL_YEAR, year)
                put(MyDBHelper.COL_PRICE, price)
            }

            val result = db.insert(MyDBHelper.TABLE_NAME, null, values)
            if (result != -1L) {
                Toast.makeText(this, "新增成功：$brand", Toast.LENGTH_SHORT).show()
                clearInput()
                queryCar()
            } else {
                Toast.makeText(this, "新增失敗：廠牌 '$brand' 已存在 (主鍵不可重複)！", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "新增錯誤：${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    // 2. 修改功能 (Update)
    private fun updateCar() {
        val brand = edBrand.text.toString().trim()
        val model = edModel.text.toString().trim()
        val yearStr = edYear.text.toString().trim()
        val priceStr = edPrice.text.toString().trim()

        if (brand.isEmpty()) {
            Toast.makeText(this, "請輸入欲修改的車子廠牌 (主鍵)！", Toast.LENGTH_SHORT).show()
            return
        }

        if (model.isEmpty() || yearStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "請填寫欲修改後的完整資料！", Toast.LENGTH_SHORT).show()
            return
        }

        val year = yearStr.toIntOrNull()
        val price = priceStr.toIntOrNull()

        if (year == null || price == null) {
            Toast.makeText(this, "年份與價格必須為有效數字！", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val values = ContentValues().apply {
                put(MyDBHelper.COL_MODEL, model)
                put(MyDBHelper.COL_YEAR, year)
                put(MyDBHelper.COL_PRICE, price)
            }

            val count = db.update(MyDBHelper.TABLE_NAME, values, "${MyDBHelper.COL_BRAND} = ?", arrayOf(brand))
            if (count > 0) {
                Toast.makeText(this, "修改成功：$brand", Toast.LENGTH_SHORT).show()
                clearInput()
                queryCar()
            } else {
                Toast.makeText(this, "修改失敗：查無廠牌為 '$brand' 的車輛資料！", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "修改錯誤：${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    // 3. 刪除功能 (Delete)
    private fun deleteCar() {
        val brand = edBrand.text.toString().trim()

        if (brand.isEmpty()) {
            Toast.makeText(this, "請輸入欲刪除的車子廠牌 (主鍵)！", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val count = db.delete(MyDBHelper.TABLE_NAME, "${MyDBHelper.COL_BRAND} = ?", arrayOf(brand))
            if (count > 0) {
                Toast.makeText(this, "刪除成功：$brand", Toast.LENGTH_SHORT).show()
                clearInput()
                queryCar()
            } else {
                Toast.makeText(this, "刪除失敗：查無廠牌為 '$brand' 的車輛資料！", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "刪除錯誤：${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    // 4. 查詢功能 (Query)
    private fun queryCar() {
        val brand = edBrand.text.toString().trim()
        itemList.clear()

        try {
            val cursor = if (brand.isEmpty()) {
                db.query(MyDBHelper.TABLE_NAME, null, null, null, null, null, null)
            } else {
                db.query(
                    MyDBHelper.TABLE_NAME,
                    null,
                    "${MyDBHelper.COL_BRAND} LIKE ?",
                    arrayOf("%$brand%"),
                    null,
                    null,
                    null
                )
            }

            if (cursor.moveToFirst()) {
                do {
                    val cBrand = cursor.getString(cursor.getColumnIndexOrThrow(MyDBHelper.COL_BRAND))
                    val cModel = cursor.getString(cursor.getColumnIndexOrThrow(MyDBHelper.COL_MODEL))
                    val cYear = cursor.getInt(cursor.getColumnIndexOrThrow(MyDBHelper.COL_YEAR))
                    val cPrice = cursor.getInt(cursor.getColumnIndexOrThrow(MyDBHelper.COL_PRICE))

                    // 格式如圖片中的展示樣式: 廠牌:Toyota   車型:RAV4   年份:2020   價格:65
                    itemList.add("廠牌:$cBrand   車型:$cModel   年份:$cYear   價格:$cPrice")
                } while (cursor.moveToNext())
            }
            cursor.close()

            adapter.notifyDataSetChanged()
            Toast.makeText(this, "查詢完成，共 ${itemList.size} 筆資料", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "查詢錯誤：${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    // 清空輸入欄位
    private fun clearInput() {
        edBrand.setText("")
        edModel.setText("")
        edYear.setText("")
        edPrice.setText("")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::db.isInitialized && db.isOpen) {
            db.close()
        }
        if (::dbHelper.isInitialized) {
            dbHelper.close()
        }
    }
}

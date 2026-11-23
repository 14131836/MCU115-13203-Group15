package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(
    context: Context,
    name: String = DB_NAME,
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = DB_VERSION
) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        private const val DB_NAME = "CarDB.db"
        private const val DB_VERSION = 1
        const val TABLE_NAME = "carTable"
        const val COL_BRAND = "brand"
        const val COL_MODEL = "model"
        const val COL_YEAR = "year"
        const val COL_PRICE = "price"
    }

    override fun onCreate(db: SQLiteDatabase) {
        createTable(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        createTable(db)
    }

    private fun createTable(db: SQLiteDatabase) {
        val createTableSql = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COL_BRAND TEXT PRIMARY KEY NOT NULL,
                $COL_MODEL TEXT NOT NULL,
                $COL_YEAR INTEGER NOT NULL,
                $COL_PRICE INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableSql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}

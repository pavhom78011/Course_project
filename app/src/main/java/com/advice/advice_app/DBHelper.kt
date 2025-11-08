package com.advice.advice_app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(val context: Context, val factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "advdb", factory, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE advices (num INT PRIMARY KEY, id INT UNIQUE, advice_text TEXT)"
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS advices")
        onCreate(db)
    }

    fun addAdvice(advice: Advice) {
        val values = ContentValues()
        values.put("id", advice.id)
        values.put("advice_text", advice.adviceText)
        val db = this.writableDatabase
        db.insertWithOnConflict("advices", null, values, SQLiteDatabase.CONFLICT_IGNORE)
        db.close()
    }

    fun clearAllAdvices() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM advices")
        db.close()
        println("Все советы удалены")
    }

    fun getAllAdvices(): List<Advice> {
        val list = ArrayList<Advice>()
        val db = readableDatabase
        db.query("advices", arrayOf("id", "advice_text"), null, null, null, null, null).use { c ->
            val idCol = c.getColumnIndexOrThrow("id")
            val textCol = c.getColumnIndexOrThrow("advice_text")
            while (c.moveToNext()) {
                list.add(Advice(c.getString(textCol), c.getInt(idCol)))
            }
        }
        db.close()
        return list
    }
}
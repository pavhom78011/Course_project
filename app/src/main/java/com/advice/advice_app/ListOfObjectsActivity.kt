package com.advice.advice_app

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class ListOfObjectsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_of_objects)

        val backButton: Button = findViewById(R.id.Back_Button)
        val delInfo: Button = findViewById(R.id.DeleteAll_From_DB)
        val sortInfo: Button = findViewById(R.id.Sort_Id)
        val dbView: ListView = findViewById(R.id.DBView)

        val db = DBHelper(this, null)
        val list = db.getAllAdvices()
        fillView(list)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        delInfo.setOnClickListener {
            val db = DBHelper(this, null)
            db.clearAllAdvices()
        }

        sortInfo.setOnClickListener {
            val sorted_list = list.sortedBy { it.id }
            fillView(sorted_list)
        }
    }

    override fun onRestart() {
        super.onRestart()
        val db = DBHelper(this, null)
        val list = db.getAllAdvices()
        fillView(list)
    }

    fun fillView(list: List<Advice>) {
        val dbView: ListView = findViewById(R.id.DBView)
        dbView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list.map { "${it.id}: ${it.adviceText}" })
    }
}
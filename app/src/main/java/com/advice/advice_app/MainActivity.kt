package com.advice.advice_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val variants: ListView = findViewById(R.id.Variants_Of_Requests)
        val welcomeLabel = findViewById<TextView>(R.id.Welcome_Text_View)
        val requestLabel = findViewById<TextView>(R.id.Write_Request_Text_View)
        val chooseView = findViewById<TextView>(R.id.Choose_View)
        val userRequest: EditText = findViewById(R.id.Request_Layout)
        val enterRequstButton: Button = findViewById(R.id.Enter_Request_Button)
        val exitButton: Button = findViewById(R.id.Exit_Button)
        val dailyAdv: Button = findViewById(R.id.Daily_Adv)

        val todos: MutableList<String> = mutableListOf()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, todos)
        variants.adapter = adapter
        adapter.insert("Random", 0)
        adapter.insert("By id", 1)
        adapter.insert("By keywords", 2)
        var txt = ""
        var advInfo: List<Advice>?
        var adv: Advice?

        variants.setOnItemClickListener { adapterView, view, i, l ->
            if(i == 1 || i == 2) {
                txt = variants.getItemAtPosition(i).toString()
                Thread.sleep(300)
                requestLabel.visibility = View.VISIBLE
                userRequest.visibility = View.VISIBLE
                enterRequstButton.visibility = View.VISIBLE
                variants.visibility = View.INVISIBLE
                chooseView.visibility = View.INVISIBLE
            }
            else {
                val requester = Request(this)
                requester.requestRandomAdvice { advice ->
                    adv = advice
                    print(adv!!.id)
                    print(" ")
                    println(adv!!.adviceText)
                    addAdviceToDB(adv!!)
                    val intent = Intent(this, ListOfObjectsActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        enterRequstButton.setOnClickListener {
            var userText = userRequest.text.toString().trim()
            if(txt == "By id") {
                val id = userText.toIntOrNull()
                if(id != null) {
                    val requester = Request(this)
                    requester.requestAdviceByID(id) { advice ->
                        adv = advice
                        if(adv != null) {
                            print(adv!!.id)
                            print(" ")
                            println(adv!!.adviceText)
                            println("this is advice")
                            addAdviceToDB(adv!!)
                        }
                        val intent = Intent(this, ListOfObjectsActivity::class.java)
                        startActivity(intent)
                    }
                    requestLabel.visibility = View.INVISIBLE
                    userRequest.visibility = View.INVISIBLE
                    enterRequstButton.visibility = View.INVISIBLE
                }
                else {
                    Toast.makeText(this, "Введите числовой id", Toast.LENGTH_LONG).show()
                }
            }
            else if(txt == "By keywords") {
                val keywords = userText
                if (keywords.isNotEmpty()) {
                    val requester = Request(this)
                    requester.requestAdviceByKeywords(keywords) { advice ->
                        advInfo = advice
                        if (advInfo != null) {
                            advInfo!!.forEach { adv -> println("id=${adv.id} advice=${adv.adviceText}"); addAdviceToDB(adv) }
                        }
                        val intent = Intent(this, ListOfObjectsActivity::class.java)
                        startActivity(intent)
                    }
                    requestLabel.visibility = View.INVISIBLE
                    userRequest.visibility = View.INVISIBLE
                    enterRequstButton.visibility = View.INVISIBLE
                }
                else {
                    Toast.makeText(this, "Введите ключевые слова", Toast.LENGTH_LONG).show()
                }
            }
            userRequest.text.clear()
        }

        exitButton.setOnClickListener {
            finishAffinity()
        }
    }

    override fun onRestart() {
        super.onRestart()
        val variants: ListView = findViewById(R.id.Variants_Of_Requests)
        val chooseView = findViewById<TextView>(R.id.Choose_View)
        variants.visibility = View.VISIBLE
        chooseView.visibility = View.VISIBLE
    }

    fun addAdviceToDB(a: Advice) {
        val db = DBHelper(this, null)
        db.addAdvice(a)
        print("совет добавлен ")
        println(a.id)
    }
}
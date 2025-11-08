package com.advice.advice_app

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class Request(private val ctx: Context) {
    fun parseKeywordRequest(json: String?): List<Advice>? {
        if (!json.isNullOrEmpty() && json.contains("message", ignoreCase = true)) {
            return null
        }
        val js = JSONObject(json)
        val list = ArrayList<Advice>()
        val items = js.getJSONArray("slips")
        for (i in 0 until items.length()) {
            val adv_info = items[i] as JSONObject
            val item = Advice(adv_info.getString("advice"),adv_info.getInt("id"))
            list.add(item)
        }
        return list
    }

    fun parseRandomOrByIDRequest(json: String?): Advice? {
        if (!json.isNullOrEmpty() && json.contains("message", ignoreCase = true)) {
            return null
        }
        val js = JSONObject(json)
        return Advice(js.getJSONObject("slip").getString("advice"), js.getJSONObject("slip").getInt("id"))
    }

    fun requestRandomAdvice(onResult: (Advice?) -> Unit) {
        val url = "https://api.adviceslip.com/advice"
        val queue = Volley.newRequestQueue(ctx)
        val request = StringRequest(Request.Method.GET, url,
            {result -> Log.d("advLog", "advice: $result")
            onResult(parseRandomOrByIDRequest(result))}, { error -> Log.d("advLog", "ERROR: $error")
            onResult(null)})
        request.setShouldCache(false)
        queue.add(request)
    }

    fun requestAdviceByID(slip_id: Int?, onResult: (Advice?) -> Unit) {
        val url = "https://api.adviceslip.com/advice/$slip_id"
        val queue = Volley.newRequestQueue(ctx)
        val request = StringRequest(Request.Method.GET, url,
            {result -> Log.d("advLog", "advice by ID: $result")
                onResult(parseRandomOrByIDRequest(result))}, { error -> Log.d("advLog", "ERROR: $error")
                onResult(null)})
        request.setShouldCache(false)
        queue.add(request)
    }

    fun requestAdviceByKeywords(slip_keywords: String?, onResult: (List<Advice>?) -> Unit) {
        val url = "https://api.adviceslip.com/advice/search/$slip_keywords"
        val queue = Volley.newRequestQueue(ctx)
        val request = StringRequest(Request.Method.GET, url,
            {result -> Log.d("advLog", "advice by keywords: $result")
                onResult(parseKeywordRequest(result))}, { error -> Log.d("advLog", "ERROR: $error")
                onResult(null)})
        request.setShouldCache(false)
        queue.add(request)
    }
}
package com.capstone.education.edubright.view.chat

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.education.edubright.BuildConfig
import com.capstone.education.edubright.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException


class ChatActivity : AppCompatActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar?.apply {
            title = getString(R.string.virtual_assistant)
            setDisplayHomeAsUpEnabled(true)
        }

        val etQuestion = findViewById<EditText>(R.id.etQuestion)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val txtResponse = findViewById<TextView>(R.id.tvResponse)

        btnSubmit.setOnClickListener {
            val question = etQuestion.text.toString().trim()
            if (question.isEmpty()) {
                Toast.makeText(this, "Please enter a question", Toast.LENGTH_SHORT).show()
            } else {
                txtResponse.text = getString(R.string.loading)
                getResponse(question) { response ->
                    runOnUiThread {
                        txtResponse.text = response
                    }
                }
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
    private fun getResponse(question: String, callback: (String) -> Unit) {
        val apiKey = BuildConfig.API_KEY
        val url = "${BuildConfig.BASE_URL}?key=$apiKey"

        val requestBody = """
            {
                "contents": [
                    {
                        "parts": [
                            {
                                "text": "$question"
                            }
                        ]
                    }
                ]
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API Error", "Request failed", e)
                runOnUiThread {
                    Toast.makeText(
                        this@ChatActivity,
                        "Failed to connect to API: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    Log.v("API Response", responseBody)
                    callback(parseResponse(responseBody))
                } else {
                    Log.e("API Error", "Response error: ${response.message}")
                    runOnUiThread {
                        Toast.makeText(
                            this@ChatActivity,
                            "Error: ${response.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })
    }

    private fun parseResponse(responseBody: String): String {
        return try {
            val jsonObject = org.json.JSONObject(responseBody)
            val candidatesArray = jsonObject.getJSONArray("candidates")
            val firstCandidate = candidatesArray.getJSONObject(0)
            val content = firstCandidate.getJSONObject("content")
            val partsArray = content.getJSONArray("parts")
            val firstPart = partsArray.getJSONObject(0)
            firstPart.getString("text")
        } catch (e: Exception) {
            Log.e("Parse Error", "Failed to parse response", e)
            "Failed to parse API response"
        }
    }
}

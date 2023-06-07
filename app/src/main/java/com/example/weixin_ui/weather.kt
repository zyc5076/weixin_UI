package com.example.weixin_ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.net.HttpURLConnection
import java.net.URL
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread

class weather : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var weatherTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        weatherTextView = findViewById(R.id.weatherTextView)
        searchButton.setOnClickListener {
            val city = searchEditText.text.toString()
            getWeather(city)
        }
    }

    private fun getWeather(city: String) {
        // 开启线程发起网络请求
        thread {
            var connection: HttpURLConnection? = null
            try {
                val url = URL("https://apis.tianapi.com/tianqi/index")
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.doOutput = true
                connection.setRequestProperty("content-type", "application/x-www-form-urlencoded")
                val outputStream = connection.outputStream
                val content = "key=c780497e1b7cb2ea8b7dc1efd8f458b2&city=$city&type=1"
                outputStream.write(content.toByteArray())
                outputStream.flush()
                outputStream.close()
                val inputStream = connection.inputStream
                val reader = inputStream.bufferedReader()
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                inputStream.close()
                // 在这里处理返回的数据
                val weatherData = response.toString()
                showResponse(weatherData)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                //关闭连接
                connection?.disconnect()
            }
        }
    }
    private fun showResponse(response: String) {
        runOnUiThread {
            // 在这里进行UI操作，将结果显示到界面上
            val responseText=findViewById<TextView>(R.id.weatherTextView)
            responseText.text = response
        }
    }
}

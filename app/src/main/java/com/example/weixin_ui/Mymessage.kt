package com.example.weixin_ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.dao.BaseActivity
import com.example.dao.MyDatabaseHelper
import org.json.JSONObject
import kotlin.concurrent.thread

class Mymessage : BaseActivity() {
    @Suppress("DEPRECATION")
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mymessage)
        // 获取 SharedPreferences 对象
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val dbHelper = MyDatabaseHelper(this, "wx.db", 8)//更新需要更改版本号大于原本号码才能执行更新
        val homeButton = findViewById<ImageButton>(R.id.home_button)
        homeButton.setOnClickListener {
            // 切换到主页
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
        val friendsButton = findViewById<ImageButton>(R.id.friends_button)
        friendsButton.setOnClickListener {
            // 切换到好友页面
            val intent = Intent(this, Friends::class.java)
            startActivity(intent)
            finish()
        }
        //退出登录
        val exitButton=findViewById<Button>(R.id.exit)
        exitButton.setOnClickListener {
            //更新数据库的用户登录状态
            val db = dbHelper.writableDatabase
            val username=prefs.getString("username","")
            val values = ContentValues().apply {
                put("login_status", 0)
            }
            val whereClause = "username = ?"
            val whereArgs = arrayOf(username)
            db.update("users", values, whereClause, whereArgs)
            db.close()
            // 获取 SharedPreferences 对象
//            val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val editor = prefs.edit()
            editor.putBoolean("remember_password", false)
            editor.putBoolean("is_login", false)
            editor.putString("username", null)
            editor.putString("password", null)
            editor.apply()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        // 初始化信息，
        val message = (application as MyApplication).message
        val name_text=findViewById<TextView>(R.id.name)
        val sex_text=findViewById<TextView>(R.id.sex)
        val age_text=findViewById<TextView>(R.id.age)
        val hobby_text=findViewById<TextView>(R.id.hobby)
        val birth_text=findViewById<TextView>(R.id.birth)
        // 获取用户登录状态，默认为未登录
        val isLogin = prefs.getBoolean("is_login", false)
        if (isLogin){
            // 初始化 Message 对象并设置用户信息
            val db = dbHelper.writableDatabase
            val username=prefs.getString("username","")
            Toast.makeText(this,username, Toast.LENGTH_SHORT).show()
            // 查询条件
            val selection = "username = ?"
            val selectionArgs = arrayOf(username)
            // 执行查询
            val cursor = db.query("users", arrayOf("username", "age", "birth", "hobby","sex"), selection, selectionArgs, null, null, null
            )
            // 获取查询结果
            if (cursor.moveToFirst()) {
                name_text.append(cursor.getString(cursor.getColumnIndex("username")))
                sex_text.append(cursor.getString(cursor.getColumnIndex("sex")))
                age_text.append(cursor.getString(cursor.getColumnIndex("age")))
                hobby_text.append(cursor.getString(cursor.getColumnIndex("hobby")))
                birth_text.append(cursor.getString(cursor.getColumnIndex("birth")))
            }
            // 关闭游标和数据库连接
            cursor.close()
            db.close()
        }
        //强制退出
        val xiaxian=findViewById<Button>(R.id.xiaxian)
        xiaxian.setOnClickListener{
            val intent = Intent("com.example.broadcastbestpractice.FORCE_OFFLINE")
            sendBroadcast(intent)
        }
        //查询天气
        val tianqi=findViewById<Button>(R.id.tianqi)
        tianqi.setOnClickListener{
        }

    }
}
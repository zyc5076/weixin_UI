package com.example.weixin_ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.example.dao.BaseActivity
import com.example.dao.MyDatabaseHelper

class login : BaseActivity() {
    @Suppress("DEPRECATION")
    @SuppressLint("Range", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //临时查询数据库
        val select1=findViewById<Button>(R.id.dbselect1)
        val dbHelper = MyDatabaseHelper(this, "wx.db", 8)//更新需要更改版本号大于原本号码才能执行更新
        select1.setOnClickListener {
            val db = dbHelper.writableDatabase
            // 查询Book表中所有的数据
            var see=""
            val cursor = db.query("users", null, null, null, null, null, null)
            if (cursor.moveToFirst()) {
                do {
                    // 遍历Cursor对象，取出数据并打印
                    val username = cursor.getString(cursor.getColumnIndex("username"))
                    val password = cursor.getString(cursor.getColumnIndex("password"))
                    val sex = cursor.getInt(cursor.getColumnIndex("sex"))
                    val birth = cursor.getDouble(cursor.getColumnIndex("birth"))
                    val age = cursor.getDouble(cursor.getColumnIndex("age"))
                    val hobby = cursor.getDouble(cursor.getColumnIndex("hobby"))
                    see=see+username+password+sex+birth+age+hobby+"_______"
                } while (cursor.moveToNext())
            }
            cursor.close()//记得关闭cursor对象
            db.close()
            Toast.makeText(this, see, Toast.LENGTH_SHORT).show()
        }
        //如果是从注册界面过来，将自动写入用户名
        val username=findViewById<EditText>(R.id.username)
        val intent=intent
        val username_before=intent.getStringExtra("username")
//        Toast.makeText(this, username_before, Toast.LENGTH_SHORT).show()
        username.setText(username_before)
        //界面跳转
        val login=findViewById<Button>(R.id.login)
        login.setOnClickListener{
            val db = dbHelper.writableDatabase
            val projection = arrayOf("password")
            val selection = "username = ?"
            val username_text=findViewById<EditText>(R.id.username).text.toString()
            val password_text=findViewById<EditText>(R.id.password).text.toString()
            val selectionArgs = arrayOf(username_text)
            val cursor = db.query("users", projection, selection, selectionArgs, null, null, null)
            var password=""
            with(cursor) {
                if (moveToNext()) {
                    password = getString(getColumnIndexOrThrow("password"))
                    if (username_text.isEmpty()){
                        Toast.makeText(this@login, "请输入用户名", Toast.LENGTH_SHORT).show()
                    }else if (password_text.isEmpty()){
                        Toast.makeText(this@login, "请输入密码", Toast.LENGTH_SHORT).show()
                    }else if(password.equals(password_text)){
//                val prefs = getPreferences(Context.MODE_PRIVATE)
                        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                        val editor = prefs.edit()
                        val rememberPass=findViewById<CheckBox>(R.id.rememberPass)
                        if (rememberPass.isChecked) { // 检查复选框是否被选中
                            editor.putBoolean("remember_password", true)
                            editor.putString("username", username.text.toString())
                            editor.putString("password", password)
                        } else {
                            editor.clear()
                            editor.putString("username", username.text.toString())
                        }
                        editor.putBoolean("is_login", true)
                        editor.apply()
                        //更新数据库的用户登录状态
                        val values = ContentValues().apply {
                            put("login_status", 1)
                        }
                        val whereClause = "username = ?"
                        val whereArgs = arrayOf(username_text)
                        db.update("users", values, whereClause, whereArgs)
//                        val intent = Intent(this@login, Home::class.java)
//                        startActivity(intent)
//                        finish()
                        // 创建子线程
                        val thread = Thread {
                            // 延时1秒
                            try {
                                Thread.sleep(1000)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                            // 打开Home界面
                            runOnUiThread {
                                val intent = Intent(this@login, Home::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                        // 启动子线程
                        thread.start()
                    }else{
                        Toast.makeText(this@login, "用户名或密码错误", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@login, "查无此人", Toast.LENGTH_SHORT).show()
                }
            }
            cursor.close()
            db.close()
        }
        val regist=findViewById<Button>(R.id.regist)
        regist.setOnClickListener {
            val intent = Intent(this, Regist::class.java)
            startActivity(intent)
        }
        //记住密码
//        val prefs = getPreferences(Context.MODE_PRIVATE)
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val isRemember = prefs.getBoolean("remember_password", false)
        if (isRemember&&username.text.isEmpty()) {
            // 将账号和密码都设置到文本框中
            val account = prefs.getString("username", "")
            val password = prefs.getString("password", "")
            val accountEdit=findViewById<EditText>(R.id.username)
            val passwordEdit=findViewById<EditText>(R.id.password)
            val rememberPass=findViewById<CheckBox>(R.id.rememberPass)
            accountEdit.setText(account)
            passwordEdit.setText(password)
            rememberPass.isChecked = true
        }
    }
}


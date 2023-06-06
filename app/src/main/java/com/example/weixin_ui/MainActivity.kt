package com.example.weixin_ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.view.View
import android.widget.*
import kotlin.math.log

class MainActivity : AppCompatActivity(), View.OnClickListener {
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val login=findViewById<Button>(R.id.login)
        login.setOnClickListener(this)
        val regist=findViewById<Button>(R.id.regist)
        regist.setOnClickListener(this)
        // 获取 SharedPreferences 对象
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        // 获取用户登录状态，默认为未登录
        val isLogin = prefs.getBoolean("is_login", false)
        // 根据用户登录状态跳转到对应的 Activity
        if (isLogin){
            startActivity(Intent(this, Home::class.java))
//            finish()
        }

    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.login ->{
                val intent = Intent(this,login::class.java)
                startActivity(intent)
            }
            R.id.regist ->{
                val intent = Intent(this,Regist::class.java)
                startActivity(intent)
            }
        }
    }
}


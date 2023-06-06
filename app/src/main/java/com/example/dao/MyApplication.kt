package com.example.weixin_ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import kotlin.math.log

class MyApplication: Application() {
    var message: Message? = null
    @SuppressLint("Range")
    @Suppress("DEPRECATION")
    override fun onCreate() {
        super.onCreate()
//        // 获取 SharedPreferences 对象
//        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
//        // 获取用户登录状态，默认为未登录
//        val isLogin = prefs.getBoolean("is_login", false)
//        // 根据用户登录状态跳转到对应的 Activity
//        val intent: Intent = if (isLogin) {
//            Intent(this, Home::class.java)
//        } else {
//            Intent(this, MainActivity::class.java)
//        }
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        startActivity(intent)

    }
}
data class Message(
    val userName: String,
    val sex: String,
    val age: String,
    val birthday: String,
    val hobby: String
)
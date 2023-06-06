package com.example.dao

import android.content.*
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.weixin_ui.R
import com.example.weixin_ui.login

open class BaseActivity : AppCompatActivity() {
    lateinit var receiver: ForceOfflineReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)
    }
    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.example.broadcastbestpractice.FORCE_OFFLINE")
        receiver = ForceOfflineReceiver()
        registerReceiver(receiver, intentFilter)
    }
    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }
    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }
    inner class ForceOfflineReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            AlertDialog.Builder(context).apply {
                setTitle("提示")
                setIcon(R.drawable.jinggao) // 设置图标
                setMessage("你的账户已在其他地方登录，请注意账号安全")
                setCancelable(false)
                setPositiveButton("确认") { _, _ ->
                    val dbHelper = MyDatabaseHelper(context, "wx.db", 8)//更新需要更改版本号大于原本号码才能执行更新
                    //获取 SharedPreferences 对象,
                    val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                    val editor = prefs.edit()
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
                    //修改登录状态信息
                    editor.putBoolean("remember_password", false)
                    editor.putBoolean("is_login", false)
                    editor.putString("username", null)
                    editor.putString("password", null)
                    editor.apply()
                    ActivityCollector.finishAll() //销毁所有Activity
                    val i = Intent(context, login::class.java)
                    context.startActivity(i) //重新启动LoginActivity
                }
                show()
            }
        }
    }
}
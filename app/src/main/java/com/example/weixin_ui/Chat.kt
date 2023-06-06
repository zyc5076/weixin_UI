package com.example.weixin_ui

import android.annotation.SuppressLint
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dao.MessageAdapter
import com.example.dao.MessageList
import com.example.dao.MyDatabaseHelper
import kotlin.concurrent.fixedRateTimer

class Chat : AppCompatActivity() {
    private val messageList = mutableListOf<MessageList>()
    private lateinit var adapter: MessageAdapter
    val dbHelper = MyDatabaseHelper(this, "wx.db", 8)//更新需要更改版本号大于原本号码才能执行更新
    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        //隐藏原有的标题栏
        supportActionBar?.hide()
        //取得传入的好友名称
        val intent = getIntent()
        val FriendName = intent.getStringExtra("FriendName")
        Toast.makeText(this, FriendName.toString(),Toast.LENGTH_SHORT).show()
        //改标题
        val title=findViewById<TextView>(R.id.title)
        title.setText(FriendName)
        //返回按钮
        val exit=findViewById<ImageView>(R.id.exit)
        exit.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
        //发送信息
        val send_text=findViewById<EditText>(R.id.send_text)
        val send=findViewById<Button>(R.id.send)
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val username=prefs.getString("username","")
        send.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            val values = ContentValues().apply {
                put("sender", username)
                put("receiver", FriendName)
                put("message", send_text.text.toString())
                put("timestamp", currentTime)
            }
            val db = dbHelper.writableDatabase
            db.insert("messages", null, values)
            db.close()
            // 清除输入框内容或执行其他操作
            send_text.text.clear()
            loadMessages(username.toString(),FriendName.toString())
        }
        //为RecyclerView设置布局管理器和适配器
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(messageList,username.toString())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        //加载消息列表
        loadMessages(username.toString(),FriendName.toString())

    }
    //重写手机自带的返回按键，返回setResult信息给上一界面更新列表
    override fun onBackPressed() {
        setResult(RESULT_OK)
        finish()
    }
    //加载消息
    private fun loadMessages(username:String,FriendName:String,) {
        val db = dbHelper.readableDatabase
        val projection = arrayOf("sender", "receiver", "message", "timestamp")
        val selection = "(sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?)"
        val selectionArgs = arrayOf(username, FriendName, FriendName, username)
        val sortOrder = "timestamp ASC"
        val cursor = db.query("messages", projection, selection, selectionArgs, null, null, sortOrder)

        messageList.clear()

        while (cursor.moveToNext()) {
            val sender = cursor.getString(cursor.getColumnIndexOrThrow("sender"))
            val receiver = cursor.getString(cursor.getColumnIndexOrThrow("receiver"))
            val message = cursor.getString(cursor.getColumnIndexOrThrow("message"))
            val timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"))
            val messageItem = MessageList(sender, receiver, message, timestamp)
            messageList.add(messageItem)
        }
        cursor.close()
        db.close()
        adapter.notifyDataSetChanged()
    }
}
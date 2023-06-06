package com.example.weixin_ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.*
import com.example.dao.FriendListAdapter
import com.example.dao.Frienditem
import com.example.dao.MyDatabaseHelper

class Friends : AppCompatActivity() {
    // 声明全局变量，记录当前用户 ID 和数据库对象
    private var currentUserId: Long = 1 // 当前用户 ID
    private lateinit var db: SQLiteDatabase
    private lateinit var listFriends: ListView
    @SuppressLint("MissingInflatedId")
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
        val homeButton = findViewById<ImageButton>(R.id.home_button)
        homeButton.setOnClickListener {
            // 切换到主页
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
        val meButton = findViewById<ImageButton>(R.id.me_button)
        meButton.setOnClickListener {
            // 切换到我的页面
            val intent = Intent(this, Mymessage::class.java)
            startActivity(intent)
            finish()
        }

        db = MyDatabaseHelper(this,"wx.db",8).writableDatabase
        // 添加一个按钮，点击后弹出对话框
        val AddFriend = findViewById<Button>(R.id.addFriend)
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val username=prefs.getString("username","")
        currentUserId=getUserIdByName(username.toString())
        AddFriend.setOnClickListener {
            // 创建对话框并设置输入框、确认按钮和取消按钮等属性
            val dialogBuilder = AlertDialog.Builder(this)
            val input = EditText(this)

            dialogBuilder.setTitle("添加好友")
            dialogBuilder.setMessage("请输入要添加的好友用户名：")
            dialogBuilder.setView(input)
            dialogBuilder.setPositiveButton("确认") { dialog, _ ->
                val username = input.text.toString()
                addFriend(username) // 在确认按钮的点击事件中调用 addFriend() 函数
                dialog.dismiss()
            }
            dialogBuilder.setNegativeButton("取消") { dialog, _ ->
                dialog.cancel()
            }
            dialogBuilder.show()
        }
        // 获取 ListView 控件实例
        listFriends = findViewById<ListView>(R.id.list_friends)
        // 查询数据库中的好友信息
        queryFriends()
        //点击view子项的事件
        listFriends.setOnItemClickListener { parent, view, position, id ->
            // 获取点击项的好友子项，再获取名称
            val friend = (listFriends.adapter as FriendListAdapter).getItem(position) as Frienditem
            val friendName = friend?.name
            // 创建跳转到新界面的意图（Intent）
            val intent = Intent(this, Friend_message::class.java)
            intent.putExtra("FriendName", friendName)
            intent.putExtra("myId", currentUserId)
            intent.putExtra("friendId", getUserIdByName(friendName.toString()))
            // 启动新界面
            startActivityForResult(intent,882)
        }
    }

    //更新好友列表，并按照顺序排序
    private fun queryFriends() {
        val friendList = mutableListOf<Frienditem>()
        val query = "SELECT users.username, users.sex FROM friends JOIN users ON friends.friend_id = users.id WHERE friends.user_id = ?"
        val cursor = db.rawQuery(query, arrayOf(currentUserId.toString()))
        while (cursor.moveToNext()) {
            val name = cursor.getString(0)
            val gender = cursor.getString(1)
            val friend = Frienditem(name, gender)
            friendList.add(friend)
        }
        cursor.close()

        // 按照数字和字母顺序对好友列表进行排序
        val comparator = Comparator<Frienditem> { friend1, friend2 ->
            val name1 = friend1.name
            val name2 = friend2.name
            if (name1.matches("\\d+".toRegex()) && name2.matches("\\d+".toRegex())) {
                // 如果都是数字，则按照数字顺序排列
                name1.toInt().compareTo(name2.toInt())
            } else {
                // 否则按照字母顺序排列
                name1.compareTo(name2)
            }
        }
        friendList.sortWith(comparator)

        val adapter = FriendListAdapter(this, friendList)
        listFriends.adapter = adapter
    }


    // 查询用户是否存在
    private fun isUserExist(username: String): Boolean {
        val query = "SELECT * FROM users WHERE username = ?"
        val cursor = db.rawQuery(query, arrayOf(username))
        val result = cursor.moveToFirst()
        cursor.close()
        return result
    }

    // 添加好友
    private fun addFriend(username: String) {
        if (isUserExist(username)) {
            val friendId = getUserIdByName(username)
            if (currentUserId.equals(friendId)){
                // 已经是好友，提示用户
                Toast.makeText(this, "不能添加自己为好友", Toast.LENGTH_SHORT).show()
            }else if (isFriendExist(currentUserId, friendId)) {
                // 已经是好友，提示用户
                Toast.makeText(this, "该用户已经是你的好友了", Toast.LENGTH_SHORT).show()
            } else {
                // 添加好友并提示用户
                addFriendship(currentUserId, friendId)
                //双相添加好友
                addFriendship(friendId, currentUserId)
                Toast.makeText(this, "成功添加 $username 为好友", Toast.LENGTH_SHORT).show()
                queryFriends()
            }
        } else {
            // 用户不存在，提示用户
            Toast.makeText(this, "用户 $username 不存在", Toast.LENGTH_SHORT).show()
        }
    }

    // 查询用户ID
    private fun getUserIdByName(username: String): Long {
        val query = "SELECT id FROM users WHERE username = ?"
        val cursor = db.rawQuery(query, arrayOf(username))
        var userId: Long? = null
        if (cursor.moveToFirst()) {
            userId = cursor.getLong(0)
        }
        cursor.close()
        return userId ?: -1
    }

    // 查询好友关系是否存在
    private fun isFriendExist(userId: Long, friendId: Long): Boolean {
        val query = "SELECT * FROM friends WHERE user_id = ? AND friend_id = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString(), friendId.toString()))
        val result = cursor.moveToFirst()
        cursor.close()
        return result
    }

    // 添加好友关系
    private fun addFriendship(userId: Long, friendId: Long) {
        val values = ContentValues()
        values.put("user_id", userId)
        values.put("friend_id", friendId)
        db.insert("friends", null, values)
    }
    //删除好友后返回界面，更新列表
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 882 && resultCode == RESULT_OK) {
            // 处理返回结果，更新好友信息列表
            queryFriends()
        }
    }
}







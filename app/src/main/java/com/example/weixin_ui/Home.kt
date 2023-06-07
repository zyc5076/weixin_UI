package com.example.weixin_ui

import ChatFriendListAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import com.example.dao.*

class Home : BaseActivity() {
    private var currentUserId: Long = 1 // 当前用户 ID
    private lateinit var listFriends: ListView
    private lateinit var db: SQLiteDatabase
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        db = MyDatabaseHelper(this,"wx.db",8).writableDatabase
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val username=prefs.getString("username","")
        currentUserId=getUserIdByName(username.toString())
        val friendsButton = findViewById<ImageButton>(R.id.friends_button)
        friendsButton.setOnClickListener {
            // 切换到好友页面
            val intent = Intent(this, Friends::class.java)
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
        //加载好友列表
        // 获取 ListView 控件实例
        listFriends = findViewById<ListView>(R.id.list_friends)
        // 获取好友列表数据
        val friendList = loadFriendList(username.toString())
        // 创建适配器
        val adapter = ChatFriendListAdapter(this,friendList)
        // 关联适配器与 ListView
        listFriends.adapter = adapter
        listFriends.setOnItemClickListener { _, _, position, _ ->
            val friend = friendList[position]
            // 从当前用户跳转到聊天界面，传递好友名称作为参数
            val intent = Intent(this, Chat::class.java)
            intent.putExtra("FriendName", friend.name)
            //启动并传rescode
            startActivityForResult(intent,888)
        }
    }

    private fun loadFriendList(username: String): List<ChatFriendList> {
        val friendList = mutableListOf<ChatFriendList>()
        // 查询好友列表
        val friendsQuery = "SELECT users.username FROM friends JOIN users ON friends.friend_id = users.id WHERE friends.user_id = ?"
        val friendsCursor = db.rawQuery(friendsQuery, arrayOf(currentUserId.toString()))
        while (friendsCursor.moveToNext()) {
            val friendName = friendsCursor.getString(0)
            // 查询最后一条聊天记录
            val messageQuery = "SELECT message, timestamp FROM messages WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) ORDER BY timestamp DESC LIMIT 1"
            val messageArgs = arrayOf(username, friendName, friendName, username)
            val messageCursor = db.rawQuery(messageQuery, messageArgs)
            var message: String? = null
            var timestamp: String? = null
            if (messageCursor.moveToFirst()) {
                message = messageCursor.getString(0)
                timestamp = messageCursor.getString(1)
            }
            messageCursor.close()
            val chatFriend = ChatFriendList(friendName, message ?: "", timestamp ?: "")
            friendList.add(chatFriend)
        }
        friendsCursor.close()

        // 按照最后一句话的时间进行排序
        val comparator = Comparator<ChatFriendList> { friend1, friend2 ->
            val timestamp1 = friend1.timestamp
            val timestamp2 = friend2.timestamp
            if (timestamp1 != null && timestamp2 != null) {
                timestamp2.compareTo(timestamp1)
            } else if (timestamp1 == null && timestamp2 == null) {
                0
            } else if (timestamp1 == null) {
                1
            } else {
                -1
            }
        }
        friendList.sortWith(comparator)

        return friendList
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
    //从chat界面返回触发列表消息更新事件
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val username=prefs.getString("username","")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 888 && resultCode == RESULT_OK) {
            // 处理返回结果，更新好友信息列表
            val friendList = loadFriendList(username.toString())
            val adapter = ChatFriendListAdapter(this, friendList)
            listFriends.adapter = adapter
        }
    }
}
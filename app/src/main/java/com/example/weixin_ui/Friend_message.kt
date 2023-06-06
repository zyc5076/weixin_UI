package com.example.weixin_ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.dao.MyDatabaseHelper

class Friend_message : AppCompatActivity() {
    val dbHelper = MyDatabaseHelper(this, "wx.db", 8)//更新需要更改版本号大于原本号码才能执行更新
    @SuppressLint("WrongViewCast", "MissingInflatedId", "Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_message)
        val exit=findViewById<ImageView>(R.id.exit)
        exit.setOnClickListener{
            finish()
        }
        //获取Extra信息
        val intent = getIntent()
        val FriendName = intent.getStringExtra("FriendName")
        val FriendId = intent.getLongExtra("friendId",0)
        val MyId = intent.getLongExtra("myId",0)
        Toast.makeText(this,"好友名："+FriendName+"好友ID"+FriendId+"我的id"+MyId,Toast.LENGTH_SHORT).show()

        // 初始化信息，
        val name_text=findViewById<TextView>(R.id.name)
        val sex_text=findViewById<TextView>(R.id.sex)
        val age_text=findViewById<TextView>(R.id.age)
        val hobby_text=findViewById<TextView>(R.id.hobby)
        val birth_text=findViewById<TextView>(R.id.birth)
        val db = dbHelper.writableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE username = ?",
            arrayOf(FriendName.toString())
        )
        if (cursor.moveToFirst()) {
            name_text.append(cursor.getString(cursor.getColumnIndex("username")))
            sex_text.append(cursor.getString(cursor.getColumnIndex("sex")))
            age_text.append(cursor.getString(cursor.getColumnIndex("age")))
            hobby_text.append(cursor.getString(cursor.getColumnIndex("hobby")))
            birth_text.append(cursor.getString(cursor.getColumnIndex("birth")))
        }
        cursor.close()
        db.close()
        //删除好友
        val delete=findViewById<Button>(R.id.delete_friend)
        delete.setOnClickListener {
            deleteFriendship(MyId,FriendId)
            setResult(RESULT_OK)
            finish()
        }

    }
    //双向删除好友
    private fun deleteFriendship(userId: Long, friendId: Long) {
        val db = dbHelper.writableDatabase
        // 删除 userId -> friendId 的记录
        val whereClause = "user_id = ? AND friend_id = ?"
        val whereArgs = arrayOf(userId.toString(), friendId.toString())
        db.delete("friends", whereClause, whereArgs)
        // 删除 friendId -> userId 的记录
        val reverseWhereArgs = arrayOf(friendId.toString(), userId.toString())
        db.delete("friends", whereClause, reverseWhereArgs)
        db.close()
    }
}
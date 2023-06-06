package com.example.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

//创建表
class MyDatabaseHelper(val context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {
    // 定义用户表结构
    private val USERS_TABLE = """
    CREATE TABLE users (
        id INTEGER PRIMARY KEY autoincrement,
        username text,
        password text,
        sex text,
        birth text,
        age text,
        hobby text,
        image text,
        login_status INTEGER DEFAULT 0 CHECK (login_status in (0, 1))
    )
"""
    // 定义好友表结构
    private val FRIENDS_TABLE = """
    CREATE TABLE friends (
        user_id INTEGER,
        friend_id INTEGER,
        PRIMARY KEY (user_id, friend_id),
        FOREIGN KEY (user_id) REFERENCES users(id),
        FOREIGN KEY (friend_id) REFERENCES users(id)
    )
"""
    //定义聊天表
    private val CHAT_TABLE = """
    CREATE TABLE messages (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        sender TEXT NOT NULL,
        receiver TEXT NOT NULL,
        message TEXT NOT NULL,
        timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
    )
"""

    override fun onCreate(db: SQLiteDatabase) {
        db?.execSQL(USERS_TABLE)
        db?.execSQL(FRIENDS_TABLE)
        db?.execSQL(CHAT_TABLE)
        Toast.makeText(context, "创建成功", Toast.LENGTH_SHORT).show()
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists users")//通过判断旧表存不存在决定删除旧表
        db.execSQL("drop table if exists friends")//通过判断旧表存不存在决定删除旧表
        db.execSQL("drop table if exists messages")//通过判断旧表存不存在决定删除旧表
        onCreate(db)//再执行oncreate创建新表
    }
}

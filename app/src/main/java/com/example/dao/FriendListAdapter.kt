package com.example.dao

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.weixin_ui.R

class FriendListAdapter(private val context: Context, private val friendList: List<Frienditem>) : BaseAdapter() {

    override fun getCount(): Int {
        return friendList.size
    }

    override fun getItem(position: Int): Any {
        return friendList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.friend_one, parent, false)
        }
        val friend = getItem(position) as Frienditem
        val userName = view?.findViewById<TextView>(R.id.username)
//        val userImage = view?.findViewById<TextView>(R.id.userImage)
        userName?.text = friend.name
//        userImage?.text = friend.gender
        // 为ListView条目添加点击事件
//        view?.setOnClickListener {
//            val intent = Intent(context, Friend_message::class.java)
//            intent.putExtra("FriendName", userName?.text.toString())
//            context.startActivity(intent)
//        }
        return view!!
    }

}

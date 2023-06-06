import android.content.Context
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.dao.ChatFriendList
import com.example.weixin_ui.R
import java.util.*

class ChatFriendListAdapter(context: Context, friendList: List<ChatFriendList>) :
    ArrayAdapter<ChatFriendList>(context, 0, friendList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        val viewHolder: ViewHolder

        if (itemView == null) {
            // Inflate布局文件
            itemView = LayoutInflater.from(context).inflate(R.layout.chat_one, parent, false)

            // 创建ViewHolder对象并将视图与ViewHolder绑定
            viewHolder = ViewHolder()
            viewHolder.friendNameTextView = itemView.findViewById(R.id.username)
            viewHolder.lastMessageTextView = itemView.findViewById(R.id.usermaseeage)
            viewHolder.timestampTextView = itemView.findViewById(R.id.messagetime)

            itemView.tag = viewHolder
        } else {
            // 从缓存的视图中获取ViewHolder对象
            viewHolder = itemView.tag as ViewHolder
        }

        // 获取当前位置的数据项
        val friend = getItem(position)

        // 将数据项绑定到布局中的视图
        viewHolder.friendNameTextView.text = friend?.name
        viewHolder.lastMessageTextView.text = friend?.message
        //格式时间
        val timestamp = friend?.timestamp?.toLongOrNull()
        if (timestamp != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formattedTime = dateFormat.format(Date(timestamp))
            viewHolder.timestampTextView.text = formattedTime
        } else {
            viewHolder.timestampTextView.text = ""
        }
        return itemView!!
    }

    // ViewHolder类用于缓存布局中的视图
    private class ViewHolder {
        lateinit var friendNameTextView: TextView
        lateinit var lastMessageTextView: TextView
        lateinit var timestampTextView: TextView
    }


}


package com.example.weixin_ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dao.MyDatabaseHelper
import java.util.regex.Pattern

class Regist : AppCompatActivity(), View.OnClickListener {
    val dbHelper = MyDatabaseHelper(this, "wx.db", 8)//更新需要更改版本号大于原本号码才能执行更新
    // 获取当前日期
    var today = Calendar.getInstance()
    var year = today.get(Calendar.YEAR)
    var month = today.get(Calendar.MONTH)
    var day = today.get(Calendar.DAY_OF_MONTH)
    //连接数据库对象
    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regist)
        //各个按钮
        val nan=findViewById<RadioButton>(R.id.nan)
        nan.setOnClickListener(this)
        val nv=findViewById<RadioButton>(R.id.nv)
        nv.setOnClickListener(this)
        val brith=findViewById<Button>(R.id.birth)
        brith.setOnClickListener(this)
        val chang=findViewById<CheckBox>(R.id.chang)
        chang.setOnClickListener(this)
        val tiao =findViewById<CheckBox>(R.id.tiao)
        tiao.setOnClickListener(this)
        val rap=findViewById<CheckBox>(R.id.rap)
        rap.setOnClickListener(this)
        val lanqiu=findViewById<CheckBox>(R.id.lanqiu)
        lanqiu.setOnClickListener(this)
        val yuanshen=findViewById<CheckBox>(R.id.yuanshen)
        yuanshen.setOnClickListener(this)
        val xiaohonshu=findViewById<CheckBox>(R.id.xiaohonshu)
        xiaohonshu.setOnClickListener(this)
        val tieba=findViewById<CheckBox>(R.id.tieba)
        tieba.setOnClickListener(this)
        val login=findViewById<Button>(R.id.login)
        login.setOnClickListener(this)
        val regist=findViewById<Button>(R.id.regist)
        regist.setOnClickListener(this)
        //监听年份edittext，获取年龄
        val year_t = findViewById<EditText>(R.id.year_t)
        val month_t = findViewById<EditText>(R.id.month_t)
        val day_t = findViewById<EditText>(R.id.day_t)
        val age=findViewById<TextView>(R.id.age)
        //在EditText文本框设置初始值
        year_t.setText(year.toString())
        month_t.setText((month+1).toString())
        day_t.setText(day.toString())
        age.setText((year-year).toString())
        //初始化年月限制器
        val filter_year = object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                val input = (dest.toString() + source.toString()).toIntOrNull() ?: return ""
                if (input < 0 || input > year) {
                    return ""
                }
                return null
            }
        }
        val filter_month = object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                val input = (dest.toString() + source.toString()).toIntOrNull() ?: return ""
                if (input < 1 || input > 12) {
                    return ""
                }
                return null
            }
        }
        //先限制年月
        year_t.filters = arrayOf(filter_year)
        month_t.filters = arrayOf(filter_month)
        //年龄动态计算+闰年2月日监控
        year_t.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 在文本内容发生改变之前执行的操作
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 在文本内容发生改变时执行的操作
            }
            override fun afterTextChanged(s: Editable?) {
                // 在文本内容已经发生改变之后执行的操作

                if (!year_t.text.toString().isEmpty()){
                    val text1=year_t.text.toString().trim().toInt()
                    age.setText((year-text1).toString())
                    numFilter()
                }
            }
        })
        //月份控制日上限监控
        month_t.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 在文本内容发生改变之前执行的操作
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 在文本内容发生改变时执行的操作
            }
            override fun afterTextChanged(s: Editable?) {
                // 在文本内容已经发生改变之后执行的操作
                if (!month_t.text.toString().isEmpty()){
                    numFilter()
                }
            }
        })
    }
    //限制年月日输入框的范围
    fun numFilter(){
        // 设定一个 InputFilter 来限制输入范围
        val year_t=findViewById<EditText>(R.id.year_t)
        val month_t = findViewById<EditText>(R.id.month_t)
        val day_t = findViewById<EditText>(R.id.day_t)
        //2月和非二月和润二月
        val filter_day_31 = object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                val input = (dest.toString() + source.toString()).toIntOrNull() ?: return ""
                if (input < 1 || input > 31) {
                    return ""
                }
                return null
            }
        }
        val filter_day_30 = object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                val input = (dest.toString() + source.toString()).toIntOrNull() ?: return ""
                if (input < 1 || input > 30) {
                    return ""
                }
                return null
            }
        }
        val filter_day_2_run = object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                val input = (dest.toString() + source.toString()).toIntOrNull() ?: return ""
                if (input < 1 || input > 29) {
                    return ""
                }
                return null
            }
        }
        val filter_day_2_norun = object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                val input = (dest.toString() + source.toString()).toIntOrNull() ?: return ""
                if (input < 1 || input > 28) {
                    return ""
                }
                return null
            }
        }
        //取出输入的年月数据
        val year_num =year_t.text.toString().trim().toInt()
        val month_num =month_t.text.toString().trim().toInt()
        val month_31 = intArrayOf(1, 3, 5, 7, 8, 10, 12)
        val month_30 = intArrayOf(4, 6, 9, 11)
        //判断闰年
        if (year_num%4==0&&year_num%100!=0){
            //能被4整除不能被100整除的闰年
            for (i in month_31) {
                if (i == month_num) {   // 如果有相同的元素，则输出相同的数字并退出循环
                    day_t.filters = arrayOf(filter_day_31)
                    return
                }
            }
            for (i in month_30) {
                if (i == month_num) {   // 如果有相同的元素，则输出相同的数字并退出循环
                    day_t.filters = arrayOf(filter_day_30)
                    return
                }
            }
            day_t.filters = arrayOf(filter_day_2_run)
            return
        }else if (year_num%400==0){
            //被400整除是润年
            for (i in month_31) {
                if (i == month_num) {   // 如果有相同的元素，则输出相同的数字并退出循环
                    day_t.filters = arrayOf(filter_day_31)
                    return
                }
            }
            for (i in month_30) {
                if (i == month_num) {   // 如果有相同的元素，则输出相同的数字并退出循环
                    day_t.filters = arrayOf(filter_day_30)
                    return
                }
            }
            day_t.filters = arrayOf(filter_day_2_run)
            return
        }else{
            //普通年
            for (i in month_31) {
                if (i == month_num) {   // 如果有相同的元素，则输出相同的数字并退出循环
                    day_t.filters = arrayOf(filter_day_31)
                    Toast.makeText(this, month_num.toString(), Toast.LENGTH_SHORT).show()
                    return
                }
            }
            for (i in month_30) {
                if (i == month_num) {   // 如果有相同的元素，则输出相同的数字并退出循环
                    day_t.filters = arrayOf(filter_day_30)
                    return
                }
            }
            day_t.filters = arrayOf(filter_day_2_norun)
            return
        }
    }
    //日期选择器事件
    @SuppressLint("ResourceType")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.nan->{
                Toast.makeText(this,"是靓仔啊",Toast.LENGTH_SHORT).show()
            }
            R.id.nv->{
                Toast.makeText(this,"是靓女啊",Toast.LENGTH_SHORT).show()
            }
            R.id.birth->{
                // 创建 DatePickerDialog 并显示
                val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                    // 选择日期后的处理逻辑
                    val day_t=findViewById<EditText>(R.id.day_t)
                    val month_t=findViewById<EditText>(R.id.month_t)
                    val year_t=findViewById<EditText>(R.id.year_t)
                    val age=findViewById<TextView>(R.id.age)
                    day_t.setText(selectedDay.toString())
                    month_t.setText((selectedMonth+1).toString())
                    year_t.setText(selectedYear.toString())
                    age.setText((year-selectedYear).toString())
                }, year, month, day)
                datePickerDialog.show()
            }
            R.id.chang->{
                Toast.makeText(this,"只应,",Toast.LENGTH_SHORT).show()
            }
            R.id.tiao->{
                Toast.makeText(this,"你,",Toast.LENGTH_SHORT).show()
            }
            R.id.rap->{
                Toast.makeText(this,"肽,",Toast.LENGTH_SHORT).show()
            }
            R.id.lanqiu->{
                Toast.makeText(this,"没,",Toast.LENGTH_SHORT).show()
            }
            R.id.yuanshen->{
                Toast.makeText(this,"原神怎么你了",Toast.LENGTH_SHORT).show()
            }
            R.id.tieba->{
                Toast.makeText(this,"打个∠先",Toast.LENGTH_SHORT).show()
            }
            R.id.xiaohonshu->{
                Toast.makeText(this,"家人们，真的绝绝子！",Toast.LENGTH_SHORT).show()
            }
            R.id.login ->{
                finish()
            }
            R.id.regist->{
                //判断用户名
                val username=findViewById<EditText>(R.id.username).text.toString()
                if (username.isEmpty()){
                    Toast.makeText(this, "你用户名呢？？？？？", Toast.LENGTH_SHORT).show()
                    return
                }
                //判断密码
                val passwd =findViewById<EditText>(R.id.password).text.toString()
                val passwd2 =findViewById<EditText>(R.id.password2).text.toString()
                if (passwd.isEmpty()){
                    Toast.makeText(this, "密码咋不写？？？", Toast.LENGTH_SHORT).show()
                    return
                }
                if (!passwd2.equals(passwd)){
                    Toast.makeText(this, "密码不一样啊", Toast.LENGTH_SHORT).show()
                    return
                }
                //判断性别
                val sex=findViewById<RadioGroup>(R.id.sex)
                if (sex.checkedRadioButtonId==-1){
                    Toast.makeText(this, "性别还没选哈", Toast.LENGTH_SHORT).show()
                    return
                }
                val sex_id=sex.checkedRadioButtonId
                val sex_button=findViewById<RadioButton>(sex_id)
                val sex_text=sex_button.text.toString()
                //判断年龄
                val age=findViewById<TextView>(R.id.age).text.toString()
                val pattern = Pattern.compile("[-+]?[0-9]+") // 数字正则表达式
                val matcher=pattern.matcher(age)
                var age_int=0
                while (matcher.find()) {
                    age_int = matcher.group().toInt()
                    // 现在，intValue 包含了文本中的一个整数
                }
                if (age_int<0){
                    Toast.makeText(this, "你还在肚子里？", Toast.LENGTH_SHORT).show()
                    return
                }else if (age_int>150){
                    Toast.makeText(this, "乌龟都活不过你！！！！", Toast.LENGTH_SHORT).show()
                    return
                }
                else{
                    //判断是否存在用户
                    val username=findViewById<EditText>(R.id.username).text.toString()
                    val db = dbHelper.writableDatabase
                    val projection = arrayOf("username")
                    val selection = "username = ?"
                    val selectionArgs = arrayOf(username)
                    val cursor = db.query("users", projection, selection, selectionArgs, null, null, null)
                    val exists = cursor.count > 0
                    cursor.close()
                    db.close()
                    if (exists) {
                        // 用户存在，进行相应的操作
                        Toast.makeText(this,"用户已存在！！！",Toast.LENGTH_SHORT).show()
                    } else {
                        // 用户不存在，进行相应的错误处理
                        //拼接生日
                        val year_text=findViewById<EditText>(R.id.year_t).text.toString()
                        val month_text=findViewById<EditText>(R.id.month_t).text.toString()
                        val day_text=findViewById<EditText>(R.id.day_t).text.toString()
                        val birth=year_text+"-"+month_text+"-"+day_text
                        //拼接爱好
                        val checkBox1=findViewById<CheckBox>(R.id.chang)
                        val checkBox2=findViewById<CheckBox>(R.id.tiao)
                        val checkBox3=findViewById<CheckBox>(R.id.rap)
                        val checkBox4=findViewById<CheckBox>(R.id.lanqiu)
                        val checkBox5=findViewById<CheckBox>(R.id.yuanshen)
                        val checkBox6=findViewById<CheckBox>(R.id.xiaohonshu)
                        val checkBox7=findViewById<CheckBox>(R.id.tieba)
                        var hobby=""
                        if (checkBox1.isChecked){
                            hobby=hobby+checkBox1.text+","
                        }
                        if (checkBox2.isChecked){
                            hobby=hobby+checkBox2.text+","
                        }
                        if (checkBox3.isChecked){
                            hobby=hobby+checkBox3.text+","
                        }
                        if (checkBox4.isChecked){
                            hobby=hobby+checkBox4.text+","
                        }
                        if (checkBox5.isChecked){
                            hobby=hobby+checkBox5.text+","
                        }
                        if (checkBox6.isChecked){
                            hobby=hobby+checkBox6.text+","
                        }
                        if (checkBox7.isChecked){
                            hobby=hobby+checkBox7.text+"."
                        }
                        //属性设置完成，进入注册动画
                        val progressBar=findViewById<ProgressBar>(R.id.jiazai)
                        val handler = Handler(Looper.getMainLooper())
                        Toast.makeText(this,"别急，我知道你很急，但是，你先别急！",Toast.LENGTH_SHORT).show()
                        progressBar.visibility=View.VISIBLE
                        //存入数据库
                        val values = ContentValues().apply {
                            // 组装数据
                            put("username", username)
                            put("password", passwd)
                            put("sex", sex_text)
                            put("birth", birth)
                            put("age", age)
                            put("hobby", hobby)
                        }
                        val db = dbHelper.writableDatabase
                        db.insert("users", null, values) //插入数据
                        db.close()
                        //显示进度条
                        handler.post(object : Runnable {
                            override fun run() {
                                if (progressBar.progress < 100) {
                                    // 更新进度条进度
                                    progressBar.progress +=1
                                    // 间隔1秒后再次更新进度条
                                    handler.postDelayed(this, 50)
                                } else {
                                    progressBar.visibility=View.GONE
                                    AlertDialog.Builder(this@Regist).apply {
                                        setTitle("注册成功了")
                                        setMessage("这么急，急急国王是吧！")
                                        setCancelable(false)
                                        setPositiveButton("去登录") { dialog, which ->
                                            val intent = Intent(this@Regist,login::class.java)
                                            progressBar.progress=0
                                            intent.putExtra("username",username)
                                            startActivity(intent)
                                            finish()
                                        }
                                        show()
                                    }
                                    // 进度条加载完成，做出相应的处理
                                }
                            }
                        })
                    }
                }
            }
        }
    }
}



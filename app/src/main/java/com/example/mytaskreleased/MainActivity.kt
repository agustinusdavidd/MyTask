package com.example.mytaskreleased

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var appGreetingTv: TextView
    private lateinit var addToDoEt: EditText
    private lateinit var addContributorEt: EditText
    private lateinit var spinner: Spinner
    private lateinit var dueToBtn: AppCompatButton
    private lateinit var timeBtn: Button
    private lateinit var addTaskBtn: Button
    val calendar = Calendar.getInstance()

    private var selectedCategory: String = ""
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private lateinit var sharedPref: SharedPreferences

    private var savedTodo: String? = ""
    private var savedContributor: String? = ""
    private var savedCategory: String? = ""
    private var savedDueTo: String? = ""
    private var savedTime: String? = ""

    private lateinit var taskNameTv: TextView
    private lateinit var detailTaskTv: TextView
    private lateinit var taskImgIv : ImageView
    private lateinit var prevTaskBtn: AppCompatButton
    private lateinit var nextTaskBtn: AppCompatButton
    private lateinit var doneTaskBtn: AppCompatButton
    private lateinit var contributor1Iv: CircleImageView
    private lateinit var contributor2Iv: CircleImageView


    private lateinit var calendarMenuBtn: AppCompatButton
    private lateinit var notificationMenuBtn: AppCompatButton
    private lateinit var moreMenuBtn: AppCompatButton

    private lateinit var taskList: MutableList<Task>
    private var currentTaskIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        spinner = findViewById(R.id.categories_spinner)
        ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
        spinner.onItemSelectedListener = this

        calendarMenuBtn = findViewById(R.id.calendar_menu_btn)
        notificationMenuBtn = findViewById(R.id.notification_menu_btn)
        moreMenuBtn = findViewById(R.id.more_menu_btn)

        dueToBtn = findViewById(R.id.due_to_btn)
        addToDoEt = findViewById(R.id.add_todo_et)
        addContributorEt = findViewById(R.id.add_contributor_et)
        appGreetingTv = findViewById(R.id.app_greeting_tv)
        timeBtn = findViewById(R.id.time_btn)
        addTaskBtn = findViewById(R.id.add_task_btn)
        prevTaskBtn = findViewById(R.id.prev_task_btn)
        nextTaskBtn = findViewById(R.id.next_task_btn)
        doneTaskBtn = findViewById(R.id.done_task_btn)
        contributor1Iv = findViewById(R.id.contrubutor1_iv)
        contributor2Iv = findViewById(R.id.contrubutor2_iv)

        taskNameTv = findViewById(R.id.task_name_tv)
        detailTaskTv = findViewById(R.id.detail_task_tv)
        taskImgIv = findViewById(R.id.task_img_iv)
        sharedPref = getPreferences(Context.MODE_PRIVATE)
        updateAppGreeting()

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            updateLable(calendar)
        }

        dueToBtn.setOnClickListener {
            DatePickerDialog(this, datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        timeBtn.setOnClickListener {
            var inputTime = ""
            val currentTime = Calendar.getInstance()
            val startHour = currentTime.get(Calendar.HOUR_OF_DAY)
            val startMinute = currentTime.get(Calendar.MINUTE)

            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener{ view, hourOfDay, minute ->
                if(hourOfDay == 0) {
                    if(minute == 0 || minute == 1 || minute == 2 || minute == 3 || minute == 4 || minute == 5 || minute == 6 || minute == 7 || minute == 8 || minute == 9 || minute == 10 || minute == 20 || minute == 30 || minute == 40 || minute == 50) {
                        inputTime = when(minute) {
                            0 -> "00:00"
                            1 -> "00:01"
                            2 -> "00:02"
                            3 -> "00:03"
                            4 -> "00:04"
                            5 -> "00:05"
                            6 -> "00:06"
                            7 -> "00:07"
                            8 -> "00:08"
                            9 -> "00:09"
                            10 -> "00:10"
                            20 -> "00:20"
                            30 -> "00:30"
                            40 -> "00:40"
                            50 -> "00:50"
                            else -> "00:00"
                        }
                        timeBtn.setText(inputTime)
                    } else {
                        inputTime = "00:$minute"
                        timeBtn.setText(inputTime)
                    }
                }
                if(minute == 0 || minute == 1 || minute == 2 || minute == 3 || minute == 4 || minute == 5 || minute == 6 || minute == 7 || minute == 8 || minute == 9 || minute == 10 || minute == 20 || minute == 30 || minute == 40 || minute == 50) {
                    inputTime = when(minute) {
                        0 -> "$hourOfDay:00"
                        1 -> "$hourOfDay:01"
                        2 -> "$hourOfDay:02"
                        3 -> "$hourOfDay:03"
                        4 -> "$hourOfDay:04"
                        5 -> "$hourOfDay:05"
                        6 -> "$hourOfDay:06"
                        7 -> "$hourOfDay:07"
                        8 -> "$hourOfDay:08"
                        9 -> "$hourOfDay:09"
                        10 -> "$hourOfDay:10"
                        20 -> "$hourOfDay:20"
                        30 -> "$hourOfDay:30"
                        40 -> "$hourOfDay:40"
                        50 -> "$hourOfDay:50"
                        else -> "$hourOfDay:00"
                    }
                    timeBtn.setText(inputTime)
                } else {
                    inputTime = "$hourOfDay:$minute"
                    timeBtn.setText(inputTime)
                }
                updateTime(inputTime)

            }, startHour, startMinute, true).show()
        }

        loadData()
        if(taskList.isNotEmpty()){
            updateFirstOpen(taskList[currentTaskIndex])
        }else {
            updateFirstOpenNullOrOne(taskList)
        }
        addTaskBtn.setOnClickListener {
            val checkedToDo = addToDoEt.text.toString() == ""
            val checkedCategory = selectedCategory == "Choose Category"
            val checkedDueTo = selectedDate == ""
            val checkedTime = selectedTime == ""

            if( !checkedToDo and !checkedCategory and !checkedDueTo and !checkedTime) {
                when(taskList.size){
                    0 -> {
                        nextTaskBtn.isVisible = false
                        prevTaskBtn.isVisible = false
                        doneTaskBtn.isVisible = true
                    }
                    else -> {
                        nextTaskBtn.isVisible = true
                        nextTaskBtn.isEnabled = true
                        prevTaskBtn.isVisible = true
                        prevTaskBtn.isEnabled = true
                        doneTaskBtn.isVisible = true
                        doneTaskBtn.isEnabled = true
                    }
                }

                saveData()
                //Membersihkan nilai input
                addToDoEt.setText("")
                addContributorEt.setText("")
                spinner.setSelection(0)
                dueToBtn.setText("Due to")
                timeBtn.setText("Time")

                updateLayout(currentTaskIndex)

                val today = Calendar.getInstance()
                calendar.set(Calendar.YEAR, today.get(Calendar.YEAR))
                calendar.set(Calendar.MONTH, today.get(Calendar.MONTH))
                calendar.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH))
            }
            if (checkedToDo) {
                Toast.makeText(this, "Insert To-do", Toast.LENGTH_SHORT).show()
            }

            if (checkedCategory) {
                Toast.makeText(this, "Insert Category", Toast.LENGTH_SHORT).show()
            }

            if (checkedDueTo) {
                Toast.makeText(this, "Insert Due-to", Toast.LENGTH_SHORT).show()
            }

            if (checkedTime) {
                Toast.makeText(this, "Insert Time", Toast.LENGTH_SHORT).show()
            }
        }

        nextTaskBtn.setOnClickListener {
            if (currentTaskIndex < taskList.size - 1) {
                currentTaskIndex++
                updateLayout(currentTaskIndex)
            }
        }

        prevTaskBtn.setOnClickListener {
            if (currentTaskIndex > 0) {
                currentTaskIndex--
                updateLayout(currentTaskIndex)
            }
        }

        Log.d("TES_TASKEMPTYMAIN", taskList.isEmpty().toString())
        Log.d("TES_TASKEMPTYMAIN2", taskList.size.toString())

        doneTaskBtn.setOnClickListener {
            doneTaskBtn.isEnabled = false
            val selectedIndex = currentTaskIndex
            val selectedTask = taskList[selectedIndex]
            taskList.removeAt(selectedIndex)

            val completeTask = sharedPref.getStringSet("COMPLETE_TASK", mutableSetOf())
            completeTask?.add(Gson().toJson(selectedTask))

            val editor = sharedPref.edit()
            editor.putStringSet("COMPLETE_TASK", completeTask)
            editor.putString("TASK_LIST", Gson().toJson(taskList))
            editor.apply()

            if(taskList.isEmpty()){
                taskNameTv.text = "There is nothing to do"
                detailTaskTv.text = "Add something to do"
                taskImgIv.setImageResource(R.drawable.img_empty)
                nextTaskBtn.isVisible = false
                nextTaskBtn.isEnabled = false
                prevTaskBtn.isVisible = false
                prevTaskBtn.isEnabled = false
                doneTaskBtn.isVisible = false
                doneTaskBtn.isEnabled = false
                contributor1Iv.isVisible = false
                contributor2Iv.isVisible = false
                Toast.makeText(this, "Task ${selectedTask.todo} is done", Toast.LENGTH_SHORT).show()
            } else if(currentTaskIndex >= taskList.size) {
                currentTaskIndex = 0
                updateLayout(currentTaskIndex)
                doneTaskBtn.isEnabled = true
                Toast.makeText(this, "Task ${selectedTask.todo} is done", Toast.LENGTH_SHORT).show()
            } else {
                updateLayout(currentTaskIndex)
                doneTaskBtn.isEnabled = true
                Toast.makeText(this, "Task ${selectedTask.todo} is done", Toast.LENGTH_SHORT).show()
            }
        }

        calendarMenuBtn.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            intent.putExtra("TASK_LIST", Gson().toJson(taskList))
            startActivity(intent)
        }

        notificationMenuBtn.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }

    }

    private fun updateFirstOpenNullOrOne(taskList: MutableList<Task>) {

        if (taskList.isEmpty() == true){
            nextTaskBtn.isVisible = false
            prevTaskBtn.isVisible = false
            contributor1Iv.isVisible = false
            contributor2Iv.isVisible = false
            doneTaskBtn.isVisible = false
        }
        else {
            nextTaskBtn.isVisible = true
            prevTaskBtn.isVisible = true
            doneTaskBtn.isVisible = true
        }
    }

    private fun updateFirstOpen(task: Task) {
        taskNameTv.text = task.todo
        detailTaskTv.text = "On ${task.dueto} at ${task.time} WIB"
        setTaskImg(task.category, taskImgIv)

        if(task.contributor.isNullOrEmpty()){
            contributor2Iv.isVisible = false
            contributor1Iv.isVisible = false
        }
    }

    private fun updateLayout(index: Int) {
        loadData()
        val task = taskList[index]

        taskNameTv.text = task.todo
        detailTaskTv.text = "On ${task.dueto} at ${task.time} WIB"
        setTaskImg(task.category, taskImgIv)

        if(task.contributor.isNullOrEmpty()){
            contributor2Iv.isVisible = false
            contributor1Iv.isVisible = false
        }
    }

    private fun saveData() {
        val insertedToDo = addToDoEt.text.toString()
        val insertedContributor = addContributorEt?.text.toString()
        val insertedCategory = spinner.selectedItem.toString()
        val task = Task(insertedToDo, insertedContributor, insertedCategory, selectedDate, selectedTime)

        val editor = sharedPref.edit()

        val taskListJson = sharedPref.getString("TASK_LIST", "[]")
        val taskList = Gson().fromJson<List<Task>>(taskListJson, object : TypeToken<List<Task>>() {}.type).toMutableList()

        taskList.add(task)

        editor.putString("TASK_LIST", Gson().toJson(taskList)).apply()
//        editor.apply {
//            putString("TODO", insertedToDo)
//            putString("CONTRIBUTOR", insertedContributor)
//            putString("CATEGORY", insertedCategory)
//            putString("DUETO", selectedDate)
//            putString("TIME", selectedTime)
//        }.apply()

        Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show()
    }

    private fun loadData() {
        val taskListJson = sharedPref.getString("TASK_LIST", "")
        taskList = if(taskListJson.isNullOrEmpty()) {
            mutableListOf<Task>()
        } else {
            Gson().fromJson<List<Task>>(taskListJson, object : TypeToken<List<Task>>() {}.type).toMutableList()
        }
//        sharedPref = getSharedPreferences("user_input", Context.MODE_PRIVATE)
//        savedTodo = sharedPref.getString("TODO", "")
//        savedContributor = sharedPref.getString("CONTRIBUTOR", "")
//        savedCategory = sharedPref.getString("CATEGORY", "")
//        savedDueTo = sharedPref.getString("DUETO", "")
//        savedTime = sharedPref.getString("TIME", "")

    }

    private fun updateAppGreeting() {
        val calendar = Calendar.getInstance()
        val name = "David"

        val greeting = when (calendar.get((Calendar.HOUR_OF_DAY))) {
            in 0..11 -> "Good Morning, $name"
            in 12..18 -> "Good Afternoon, $name"
            else -> "Good Night, $name"
        }

        appGreetingTv.setText(greeting)
    }

    private fun updateLable(calendar: Calendar) {
        val dateFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        dueToBtn.setText(sdf.format(calendar.time))

        selectedDate = sdf.format(calendar.time)
    }

    private fun setTaskImg(category: String, taskImgIv: ImageView) {
        when(category) {
            "Pekerjaan" -> taskImgIv.setImageResource(R.drawable.img_work)
            "Makan" -> taskImgIv.setImageResource(R.drawable.img_eat)
            "Belajar" -> taskImgIv.setImageResource(R.drawable.img_belajar)
            "Jalan-jalan" -> taskImgIv.setImageResource(R.drawable.img_jalan_jalan)
            "Belanja" -> taskImgIv.setImageResource(R.drawable.img_shopping)
            "Olahraga" -> taskImgIv.setImageResource(R.drawable.img_workout)
            "Keuangan" -> taskImgIv.setImageResource(R.drawable.img_bank)
            "Istirahat" -> taskImgIv.setImageResource(R.drawable.img_istirahat)
            "Quality time" -> taskImgIv.setImageResource(R.drawable.img_quality_time)
            "Lain-lain" -> taskImgIv.setImageResource(R.drawable.img_etc)
            else -> taskImgIv.setImageResource(R.drawable.img_empty)
        }
    }

    private fun updateTime(inputTime: String) {
        selectedTime = inputTime
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(parent?.getItemAtPosition(position)?.equals("Choose Category") == true){
            Toast.makeText(applicationContext, "Please choose a category", Toast.LENGTH_SHORT).show()
            spinner.setSelection(0)
        }
        selectedCategory = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        spinner.setSelection(0)
    }
}

package com.example.mytaskreleased

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class CalendarActivity : AppCompatActivity() {

    private lateinit var dateRv: RecyclerView
    private lateinit var timeRv: RecyclerView
    private lateinit var dateHeaderTv: TextView
    private lateinit var dateMonthTv: TextView
    private lateinit var backBtn: Button
    private lateinit var sharedPref: SharedPreferences

    private val listHour = ArrayList<Hour>()
    private lateinit var taskList: MutableList<Task>
    private lateinit var eventList: MutableList<Events>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        val nextBtn: Button = findViewById(R.id.nextBtn)
        val prevBtn: Button = findViewById(R.id.prevBtn)
        val calendarBtn: Button = findViewById(R.id.calendarBtn)

        dateRv = findViewById(R.id.dateRv)
        dateMonthTv = findViewById(R.id.dateMonthTv)
        dateHeaderTv = findViewById(R.id.dateHeaderTv)
        backBtn = findViewById(R.id.backBtn)

        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val dateFormat = SimpleDateFormat("MMMM, dd", Locale.getDefault())
        dateHeaderTv.text = dateFormat.format(Calendar.getInstance().time).toString()

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        dateRv.layoutManager = layoutManager

        val listDateAdapter = ListDateAdapter()
        dateRv.adapter = listDateAdapter

        val today = Calendar.getInstance()
        val dateNow = Calendar.getInstance()

        nextBtn.setOnClickListener {
            val newDate = listDateAdapter.nextStartDate()
            updateMonthTv(newDate)
        }

        prevBtn.setOnClickListener {
            val newDate = listDateAdapter.prevStartDate()
            updateMonthTv(newDate)
        }

        calendarBtn.setOnClickListener {
            val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                listDateAdapter.updateStartDate(selectedDate)
            }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))

            datePicker.datePicker.minDate = today.timeInMillis
            datePicker.show()
        }

        val taskListJson = intent.getStringExtra("TASK_LIST")
        val taskList = Gson().fromJson<List<Task>>(taskListJson, object : TypeToken<List<Task>>() {}.type).toMutableList()

//        sharedPref = getSharedPreferences("TASK_LIST", Context.MODE_PRIVATE)
//        taskList = Gson().fromJson<List<Task>>(sharedPref.getString("TASK_LIST", "[]"), object : TypeToken<List<Task>>() {}.type).toMutableList()

//        val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
//        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

        Log.d("TES_TASKEMPTY", taskList.isEmpty().toString())
        Log.d("TES_CALENDAR", taskList.size.toString())
        for (task in taskList) {
            val contributor = task.contributor

            val eventDueTo = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(task.dueto)
            val eventTime = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(task.time)

            val event = Events(task.todo, contributor, eventDueTo, eventTime)
            Log.d("TES_CALENDAR2", event.Title)
            Log.d("TES_CALENDAR3", event.Contributor)
            Log.d("TES_CALENDAR4", event.Date.toString())
            Log.d("TES_CALENDAR5", event.time.toString())
            Log.d("TES_CALENDAR6", (event.time is Date).toString())
            Log.d("TES_CALENDAR7", (event.time is Date).toString())
//            eventList.add(event)
            Log.d("TES_CALENDAR8", eventList.size.toString())
        }


//        if(taskList.isNotEmpty()){
//            for (task in taskList) {
//                val contributor = task.contributor
//                val eventDueTo = dateFormatter.parse(task.dueto)
//                val eventTime = timeFormatter.parse(task.time)
//
//                val event = Events(task.todo, contributor, eventDueTo, eventTime)
//                eventList.add(event)
//            }
//        }

        Log.d("TES_TASKEMPTY2", taskList.isEmpty().toString())
        Log.d("TES_TASKEMPTY3", eventList.isEmpty().toString())

        timeRv = findViewById(R.id.timeRv)
        timeRv.setHasFixedSize(true)

        listHour.addAll(getListHour())
        timeRv.layoutManager = LinearLayoutManager(this)
        val listHourAdapter = ListHourAdapter(listHour, eventList)
        timeRv.adapter = listHourAdapter

        supportActionBar?.hide()
    }

    fun updateMonthTv(newDate: java.util.Date){
        val dateFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val monthName = dateFormat.format(newDate)

        dateMonthTv.text = monthName

    }

    private fun getListHour(): ArrayList<Hour> {
        val dataHour = resources.getStringArray(R.array.listHour)
        val local = "WIB"

        val listHour = ArrayList<Hour>()
        for(i in dataHour.indices) {
            val time = Hour(dataHour[i], local)
            listHour.add(time)
        }
        return listHour
    }
}
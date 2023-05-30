package com.example.mytaskreleased

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ListDateAdapter () : RecyclerView.Adapter<ListDateAdapter.ListViewHolder>() {
    private val startCalendar = Calendar.getInstance() //New
    var selectedDate = Calendar.getInstance()

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCalendarDay: TextView = itemView.findViewById(R.id.tv_calendar_day)
        val tvCalendarDate: TextView = itemView.findViewById(R.id.tv_calendar_date)
        val cardDate: LinearLayout = itemView.findViewById(R.id.cardDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_calendar_date_item, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val calendar = Calendar.getInstance()
        calendar.time = startCalendar.time //New
        calendar.add(Calendar.DAY_OF_YEAR, position)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val dayText = when(dayOfWeek) {
            Calendar.SUNDAY -> "SUN"
            Calendar.MONDAY -> "MON"
            Calendar.TUESDAY -> "TUE"
            Calendar.WEDNESDAY -> "WED"
            Calendar.THURSDAY -> "THU"
            Calendar.FRIDAY -> "FRI"
            Calendar.SATURDAY -> "SAT"
            else -> ""
        }

        holder.tvCalendarDay.text = dayText
        holder.tvCalendarDate.text = dayOfMonth.toString()

        if(startCalendar == calendar) {
            holder.cardDate.setBackgroundResource(R.drawable.border)
            holder.tvCalendarDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            holder.tvCalendarDay.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        }
    }

    override fun getItemCount(): Int = 7

    fun nextStartDate() : java.util.Date {
        startCalendar.add(Calendar.DAY_OF_YEAR, 7)
        val newDate = startCalendar.time
        notifyDataSetChanged()

        return newDate
    }

    fun prevStartDate() : java.util.Date {
        startCalendar.add(Calendar.DAY_OF_YEAR, -7)

        val today = Calendar.getInstance()
        if(startCalendar.before(today)) {
            startCalendar.time = today.time
        }

        val newDate = startCalendar.time
        notifyDataSetChanged()

        return newDate
    }

    fun getMonthName(month: Int): String {
        val monthNames = arrayOf(
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        )
        return monthNames[month]
    }

    fun updateStartDate(selectedDate: Calendar){
        val today = Calendar.getInstance()
        today.time = selectedDate.time

        val dayOfWeek = today.get(Calendar.DAY_OF_WEEK)
        val diff = (dayOfWeek - Calendar.SUNDAY + 7) % 7

        today.add(Calendar.DAY_OF_YEAR, -diff)

        startCalendar.time = today.time
        notifyDataSetChanged()
    }
}
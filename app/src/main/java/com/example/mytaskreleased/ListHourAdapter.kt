package com.example.mytaskreleased

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class ListHourAdapter(private val listHour: ArrayList<Hour>, private val listEvent: MutableList<Events>) : RecyclerView.Adapter<ListHourAdapter.ListViewHolder>() {

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHourDay: TextView = itemView.findViewById(R.id.tv_hour_day)
        val tvLocal: TextView = itemView.findViewById(R.id.tv_local)
        val event1: LinearLayout = itemView.findViewById(R.id.event1)
        val event2: LinearLayout = itemView.findViewById(R.id.event2)

        val task1tv: TextView = itemView.findViewById(R.id.task1Tv)
        val task2tv: TextView = itemView.findViewById(R.id.task2Tv)
        val contributor11iv: CircleImageView = itemView.findViewById(R.id.contributor11Iv)
        val contributor12iv: CircleImageView = itemView.findViewById(R.id.contributor12Iv)
        val contributor13iv: CircleImageView = itemView.findViewById(R.id.contributor13Iv)
        val contributor21iv: CircleImageView = itemView.findViewById(R.id.contributor21Iv)
        val contributor22iv: CircleImageView = itemView.findViewById(R.id.contributor22Iv)
        val contributor23iv: CircleImageView = itemView.findViewById(R.id.contributor23Iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_task_by_time_item, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (hour, local) = listHour[position]
        holder.tvHourDay.text = hour
        holder.tvLocal.text = local

        when(listEvent.size){
            0 -> {
                hideEvent(holder.event1)
                hideEvent(holder.event2)
            } 1 -> {
                setEvent(holder.event1, holder.task1tv, holder.contributor11iv, holder.contributor12iv, holder.contributor13iv, listEvent[0])
                hideEvent(holder.event2)
            } else -> {
                setEvent(holder.event1, holder.task1tv, holder.contributor11iv, holder.contributor12iv, holder.contributor13iv, listEvent[0])
                setEvent(holder.event2, holder.task2tv, holder.contributor21iv, holder.contributor22iv, holder.contributor23iv, listEvent[1])
            }
        }

    }

    private fun setEvent(event: LinearLayout, textView : TextView, contributor1 : CircleImageView, contributor2 : CircleImageView, contributor3 : CircleImageView, events: Events) {
        val checkContributor = events.Contributor == ""
        event.isVisible = true
        textView.text = events.Title
        if(!checkContributor){
            contributor1.setBackgroundResource(R.drawable.img_chris)
            contributor2.setBackgroundResource(R.drawable.img_chris)
            contributor3.setBackgroundResource(R.drawable.img_chris)
        }
    }

    private fun hideEvent(event: LinearLayout) {
        event.isVisible = false
    }

    override fun getItemCount(): Int {
        return listHour.size
    }

    private fun setEvents(view: View, eventList: ArrayList<Events>){
        if(eventList.isEmpty()){

        }
    }

}
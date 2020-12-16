package com.theevilroot.vmsis.simulator.core

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theevilroot.vmsis.simulator.R
import kotlinx.android.synthetic.main.i_stat.view.*

class StatHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

    fun bind(name: String, value: String) = with(itemView) {
        stat_name.text = name
        stat_value.text = value
    }

}

class StatsAdapter : RecyclerView.Adapter<StatHolder>() {

    private val stats = mutableListOf<Pair<String, String>>()

    fun updateStats(list: List<Pair<String, String>>) {
        stats.clear()
        stats.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatHolder =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.i_stat, parent, false)
            .let(::StatHolder)

    override fun onBindViewHolder(holder: StatHolder, position: Int) {
        stats.getOrNull(position)
            ?.let { (k, v) -> holder.bind(k, v) }
    }

    override fun getItemCount(): Int {
        return stats.size
    }

}

fun RecyclerView.setupAsStats(statsAdapter: StatsAdapter) {
    layoutManager = GridLayoutManager(context, 3,
        GridLayoutManager.VERTICAL, false)
    adapter = statsAdapter
}

fun createStatsAdapter() = lazy { StatsAdapter() }
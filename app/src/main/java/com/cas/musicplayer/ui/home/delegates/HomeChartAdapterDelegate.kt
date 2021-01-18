package com.cas.musicplayer.ui.home.delegates

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.common.recyclerview.PagerIndicatorView
import com.cas.delegatedadapter.AdapterDelegate
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.ChartModel
import com.cas.musicplayer.domain.model.HomeItem
import com.cas.musicplayer.ui.home.adapters.HomeChartAdapter

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */
class HomeChartAdapterDelegate : AdapterDelegate<List<DisplayableItem>>() {

    val adapter by lazy { HomeChartAdapter() }

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is HomeItem.ChartItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.home_chart_view)
        return HomeListChartViewHolder(view)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val chartItem = items[position] as HomeItem.ChartItem
        (holder as HomeListChartViewHolder).bind(chartItem.charts)
    }

    inner class HomeListChartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
            val pagerIndicator: PagerIndicatorView = view.findViewById(R.id.pagerIndicator)
            recyclerView.adapter = adapter
            PagerSnapHelper().attachToRecyclerView(recyclerView)
            pagerIndicator.withRecyclerView(recyclerView)
        }

        fun bind(charts: List<ChartModel>) {
            adapter.dataItems = charts.toMutableList()
        }
    }
}


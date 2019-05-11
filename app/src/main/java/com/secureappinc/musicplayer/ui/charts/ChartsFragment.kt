package com.secureappinc.musicplayer.ui.charts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.utils.gone
import kotlinx.android.synthetic.main.fragment_charts.view.*
import kotlinx.android.synthetic.main.fragment_genres.*

/**
 **********************************
 * Created by Abdelhadi on 4/26/19.
 **********************************
 */
class ChartsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_charts, container, false)
        view.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = "Charts"

        val collapsingToolbar =
            activity?.findViewById<CollapsingToolbarLayout>(com.secureappinc.musicplayer.R.id.collapsingToolbar)

        collapsingToolbar?.isTitleEnabled = false

        val rltContainer = activity?.findViewById<RelativeLayout>(com.secureappinc.musicplayer.R.id.rltContainer)
        rltContainer?.gone()

        val viewModel = ViewModelProviders.of(this).get(ChartsViewModel::class.java)

        val linearLayoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = linearLayoutManager

        val adapter = ChartsAdapter(mutableListOf())
        recyclerView.adapter = adapter

        viewModel.charts.observe(this, Observer { charts ->
            adapter.items = charts.toMutableList()
        })

        viewModel.chartDetail.observe(this, Observer { chart ->
            adapter.updateChart(chart)
        })

        viewModel.loadAllCharts()
    }
}
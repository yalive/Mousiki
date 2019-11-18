package com.cas.musicplayer.ui.charts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.musicplayer.utils.Extensions.injector
import com.cas.musicplayer.utils.gone
import com.cas.musicplayer.utils.observe
import com.cas.musicplayer.viewmodel.viewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.fragment_charts.*
import kotlinx.android.synthetic.main.fragment_charts.view.*

/**
 **********************************
 * Created by Abdelhadi on 4/26/19.
 **********************************
 */
open class ChartsFragment : Fragment() {

    open val viewModel by viewModel { injector.chartsViewModel }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_charts, container, false)
        view.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Charts"
        val collapsingToolbar =
            activity?.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)
        collapsingToolbar?.isTitleEnabled = false
        val rltContainer = activity?.findViewById<RelativeLayout>(R.id.rltContainer)
        rltContainer?.gone()

        val linearLayoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = linearLayoutManager
        val adapter = ChartsAdapter()
        recyclerView.adapter = adapter

        observe(viewModel.charts, adapter::submitList)
        observe(viewModel.chartDetail, adapter::updateChart)
    }
}
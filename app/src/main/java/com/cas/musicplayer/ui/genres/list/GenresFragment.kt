package com.cas.musicplayer.ui.genres.list

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.BaseFragment
import com.cas.musicplayer.utils.Extensions.injector
import com.cas.musicplayer.utils.dpToPixel
import com.cas.musicplayer.utils.gone
import com.cas.musicplayer.utils.observe
import com.cas.musicplayer.viewmodel.viewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.fragment_genres.*

/**
 **********************************
 * Created by Abdelhadi on 4/26/19.
 **********************************
 */
class GenresFragment : BaseFragment<GenresViewModel>() {
    override val layoutResourceId: Int = R.layout.fragment_genres
    override val viewModel by viewModel { injector.genresViewModel }

    private val adapter = GenresAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setTitle(R.string.genres)
        val collapsingToolbar =
            activity?.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)
        collapsingToolbar?.isTitleEnabled = false
        val rltContainer =
            activity?.findViewById<RelativeLayout>(R.id.rltContainer)
        rltContainer?.gone()
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.layoutManager = gridLayoutManager
        val spacingDp = requireActivity().dpToPixel(8f)
        recyclerView.addItemDecoration(
            GenresItemSpacing(
                3,
                spacingDp,
                true,
                0
            )
        )
        recyclerView.adapter = adapter
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position == 0) {
                    return 3
                } else if (position == 1) {
                    return 2
                } else {
                    return 1
                }
            }
        }
        viewModel.loadAllGenres()
        observeViewModel()
    }

    private fun observeViewModel() {
        observe(viewModel.genres) { genres ->
            adapter.dataItems = genres.toMutableList()
        }
    }
}
package com.cas.musicplayer.ui.genres

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.cas.common.extensions.observe
import com.cas.common.fragment.BaseFragment
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.utils.dpToPixel
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
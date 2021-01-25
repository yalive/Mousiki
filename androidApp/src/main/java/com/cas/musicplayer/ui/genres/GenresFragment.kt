package com.cas.musicplayer.ui.genres

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.cas.common.dpToPixel
import com.cas.common.extensions.observe
import com.cas.common.fragment.BaseFragment
import com.cas.common.recyclerview.MarginItemDecoration
import com.cas.common.recyclerview.itemsMarginDecorator
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.delegateadapter.BaseDelegationAdapter
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentGenresBinding
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.ui.searchyoutube.GenreAdapterDelegate
import com.cas.musicplayer.utils.viewBinding

/**
 **********************************
 * Created by Abdelhadi on 4/26/19.
 **********************************
 */
class GenresFragment : BaseFragment<GenresViewModel>(
    R.layout.fragment_genres
) {

    override val viewModel by viewModel { injector.genresViewModel }
    override val screenTitle: String by lazy {
        getString(R.string.genres)
    }
    private val binding by viewBinding(FragmentGenresBinding::bind)

    private val adapter by lazy {
        val delegates = listOf(
            HeaderGenreDelegate(),
            GenreAdapterDelegate(clickItemDestination = R.id.action_genresFragment_to_playlistVideosFragment)
        )
        object : BaseDelegationAdapter(delegates) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setTitle(R.string.genres)
        val eightDp = dpToPixel(8)
        binding.recyclerView.itemsMarginDecorator(MarginItemDecoration(
            horizontalMargin = eightDp,
            verticalMargin = eightDp,
            topMarginProvider = { position ->
                when {
                    position == 0 -> eightDp
                    viewModel.isHeader(position) -> eightDp * 6
                    else -> eightDp
                }
            }
        ))
        binding.recyclerView.adapter = adapter
        (binding.recyclerView.layoutManager as GridLayoutManager).spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when {
                        viewModel.isHeader(position) -> 2
                        else -> 1
                    }
                }
            }
        adjustStatusBarWithTheme()
        observeViewModel()
    }

    private fun observeViewModel() {
        observe(viewModel.genres) { genres ->
            adapter.dataItems = genres.toMutableList()
        }
    }
}
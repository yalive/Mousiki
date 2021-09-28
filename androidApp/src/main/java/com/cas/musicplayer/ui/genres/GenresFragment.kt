package com.cas.musicplayer.ui.genres

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import com.cas.common.dpToPixel
import com.cas.common.recyclerview.MarginItemDecoration
import com.cas.common.recyclerview.itemsMarginDecorator
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentGenresBinding
import com.cas.musicplayer.delegateadapter.MousikiAdapter
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.base.adjustStatusBarWithTheme
import com.cas.musicplayer.ui.base.setupToolbar
import com.cas.musicplayer.ui.searchyoutube.GenreAdapterDelegate
import com.cas.musicplayer.utils.DeviceInset
import com.cas.musicplayer.utils.viewBinding

/**
 **********************************
 * Created by Abdelhadi on 4/26/19.
 **********************************
 */
class GenresFragment : BaseFragment<GenresViewModel>(
    R.layout.fragment_genres
) {

    override val screenName: String = "GenresFragment"
    override val viewModel by viewModel { Injector.genresViewModel }
    private val binding by viewBinding(FragmentGenresBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = MousikiAdapter(
            listOf(
                HeaderGenreDelegate(),
                GenreAdapterDelegate(clickItemDestination = R.id.action_genresFragment_to_playlistVideosFragment)
            )
        )

        setupToolbar(binding.toolbarView.toolbar, R.string.genres)
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
        observe(viewModel.genres, adapter::submitList)
        observe(DeviceInset) { inset ->
            binding.root.updatePadding(top = inset.top)
        }
    }
}
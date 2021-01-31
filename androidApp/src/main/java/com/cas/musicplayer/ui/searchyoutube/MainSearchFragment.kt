package com.cas.musicplayer.ui.searchyoutube

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.cas.common.dpToPixel
import com.cas.common.extensions.observe
import com.cas.common.extensions.observeEvent
import com.cas.common.extensions.onClick
import com.cas.common.fragment.BaseFragment
import com.cas.common.recyclerview.MarginItemDecoration
import com.cas.common.viewmodel.activityViewModel
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentMainSearchBinding
import com.cas.musicplayer.delegateadapter.BaseDelegationAdapter
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.utils.navigateSafeAction
import com.cas.musicplayer.utils.viewBinding

/**
 ***************************************
 * Created by Y.Abdelhadi on 3/26/20.
 ***************************************
 */
class MainSearchFragment : BaseFragment<MainSearchViewModel>(
    R.layout.fragment_main_search
) {
    override val viewModel by viewModel { Injector.mainSearchViewModel }
    private val searchGenresAdapter by lazy { SearchGenresAdapter() }

    private val binding by viewBinding(FragmentMainSearchBinding::bind)

    private val mainViewModel by activityViewModel {
        Injector.mainViewModel
    }
    private val bottomPlayerSpace by lazy {
        requireContext().resources.getDimensionPixelSize(R.dimen.padding_for_player_space)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = searchGenresAdapter
        binding.recyclerView.addItemDecoration(
            MarginItemDecoration(
                verticalMargin = dpToPixel(8),
                horizontalMargin = dpToPixel(8),
                bottomMarginProvider = { position ->
                    if (position == searchGenresAdapter.itemCount - 2 || position == searchGenresAdapter.itemCount - 1) bottomPlayerSpace
                    else dpToPixel(8)
                }
            )
        )
        adjustStatusBarWithTheme()
        observeViewModel()
        binding.btnStartSearch.onClick {
            startSearch()
        }
        observeEvent(mainViewModel.doubleClickSearch) {
            startSearch()
        }
    }

    override fun withToolbar(): Boolean = false

    private fun observeViewModel() {
        observe(viewModel.genres) { genres ->
            searchGenresAdapter.dataItems = genres.toMutableList()
        }
    }

    private fun startSearch() {
        findNavController().navigateSafeAction(R.id.action_mainSearchFragment_to_searchYoutubeFragment)
    }
}

class SearchGenresAdapter : BaseDelegationAdapter(
    listOf(GenreAdapterDelegate())
)
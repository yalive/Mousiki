package com.cas.musicplayer.ui.searchyoutube

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.fragment.findNavController
import com.cas.common.dpToPixel
import com.cas.common.extensions.onClick
import com.cas.common.recyclerview.MarginItemDecoration
import com.cas.common.viewmodel.activityViewModel
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentMainSearchBinding
import com.cas.musicplayer.delegateadapter.MousikiAdapter
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.tmp.observeEvent
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.utils.DeviceInset
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

        observe(DeviceInset) { inset ->
            val constraintSet = binding.motionLayout.getConstraintSet(R.id.end)
            constraintSet.setMargin(
                R.id.btnStartSearch,
                ConstraintSet.TOP,
                inset.top + dpToPixel(8)
            )
        }
    }

    override fun withToolbar(): Boolean = false

    private fun observeViewModel() {
        observe(viewModel.genres, searchGenresAdapter::submitList)
    }

    private fun startSearch() {
        findNavController().navigateSafeAction(R.id.action_mainSearchFragment_to_searchYoutubeFragment)
    }
}

class SearchGenresAdapter : MousikiAdapter(
    listOf(GenreAdapterDelegate())
)
package com.cas.musicplayer.ui.searchyoutube

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.cas.common.dpToPixel
import com.cas.common.extensions.observe
import com.cas.common.extensions.onClick
import com.cas.common.fragment.BaseFragment
import com.cas.common.recyclerview.MarginItemDecoration
import com.cas.common.viewmodel.viewModel
import com.cas.delegatedadapter.BaseDelegationAdapter
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import kotlinx.android.synthetic.main.main_search_fragment.*

/**
 ***************************************
 * Created by Y.Abdelhadi on 3/26/20.
 ***************************************
 */
class MainSearchFragment : BaseFragment<MainSearchViewModel>() {
    override val viewModel by viewModel { injector.mainSearchViewModel }
    override val layoutResourceId: Int = R.layout.main_search_fragment

    private val searchGenresAdapter by lazy { SearchGenresAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = searchGenresAdapter
        recyclerView.addItemDecoration(
            MarginItemDecoration(
                verticalMargin = dpToPixel(8),
                horizontalMargin = dpToPixel(8)
            )
        )
        lightStatusBar()
        observeViewModel()
        searchBtnView.onClick {
            findNavController().navigate(R.id.action_mainSearchFragment_to_searchYoutubeFragment)
        }
    }

    override fun withToolbar(): Boolean = false

    private fun observeViewModel() {
        observe(viewModel.genres) { genres ->
            searchGenresAdapter.dataItems = genres.toMutableList()
        }
    }
}

class SearchGenresAdapter : BaseDelegationAdapter(
    listOf(SearchGenreAdapterDelegate())
)
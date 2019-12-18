package com.cas.musicplayer.ui.popular


import android.os.Bundle
import android.view.View
import com.cas.common.extensions.observe
import com.cas.common.fragment.BaseFragment
import com.cas.common.recyclerview.FirstItemMarginDecoration
import com.cas.common.resource.Resource
import com.cas.common.viewmodel.viewModel
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.utils.dpToPixel
import com.cas.musicplayer.utils.loadAndBlurImage
import com.cas.musicplayer.utils.loadImage
import kotlinx.android.synthetic.main.fragment_new_release.*


class PopularSongsFragment : BaseFragment<PopularSongsViewModel>() {
    override val viewModel by viewModel { injector.popularSongsViewModel }
    override val layoutResourceId: Int = R.layout.fragment_new_release

    private val adapter by lazy {
        PopularSongsAdapter(
            onVideoSelected = { musicTrack ->
                val mainActivity = requireActivity() as MainActivity
                mainActivity.collapseBottomPanel()
                viewModel.onClickTrack(musicTrack)
            }, onClickMoreOptions = { musicTrack ->
                val bottomSheetFragment = FvaBottomSheetFragment()
                val bundle = Bundle()
                bundle.putString("MUSIC_TRACK", injector.gson.toJson(musicTrack))
                bottomSheetFragment.arguments = bundle
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter
        observeViewModel()
        recyclerView.addOnScrollListener(EndlessRecyclerOnScrollListener {
            viewModel.loadMoreSongs()
        })
        recyclerView.addItemDecoration(FirstItemMarginDecoration(verticalMargin = dpToPixel(32)))
        btnPlayAll.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.collapseBottomPanel()
            viewModel.onClickTrackPlayAll()
        }
    }

    override fun withToolbar(): Boolean = false

    private fun observeViewModel() {
        observe(viewModel.newReleases, this::updateUI)
    }

    private fun updateUI(resource: Resource<List<DisplayableItem>>) {
        when (resource) {
            is Resource.Loading -> {
                //progressBar.visible()
            }
            is Resource.Failure -> {
                //progressBar.gone()
            }
            is Resource.Success -> {
                val newList = resource.data
                //progressBar.isInvisible = true
                if (adapter.dataItems.isEmpty() && newList.isNotEmpty()) {
                    val videoItem = newList[0] as DisplayedVideoItem
                    imgArtist.loadImage(videoItem.songImagePath)
                    background.loadAndBlurImage(videoItem.songImagePath)
                    txtPrimaryTitle.text = "New Releases"
                }
                val diffCallback = SongsDiffUtil(adapter.dataItems, resource.data)
                adapter.submitList(newList, diffCallback)
                txtNumberOfSongs.text = "${newList.size} Songs"
            }
        }
    }
}


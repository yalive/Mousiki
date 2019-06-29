package com.secureappinc.musicplayer.ui.searchyoutube.videos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.base.common.Status
import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.ui.MainActivity
import com.secureappinc.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.secureappinc.musicplayer.ui.searchyoutube.SearchYoutubeFragment
import com.secureappinc.musicplayer.utils.Extensions.injector
import com.secureappinc.musicplayer.utils.gone
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_new_release.*

/**
 **********************************
 * Created by Abdelhadi on 4/24/19.
 **********************************
 */
class YTSearchVideosFragment : Fragment(), YTSearchVideosAdapter.OnItemClickListener {

    val TAG = "NewReleaseFragment"

    lateinit var adapter: YTSearchVideosAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_yt_search_videos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val collapsingToolbar = activity?.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)

        collapsingToolbar?.isTitleEnabled = true

        val rltContainer = activity?.findViewById<RelativeLayout>(R.id.rltContainer)

        val imgCollapsed = activity?.findViewById<CircleImageView>(R.id.imgCollapsed)

        rltContainer?.gone()

        adapter = YTSearchVideosAdapter(listOf(), this) {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.collapseBottomPanel()
        }
        recyclerView.adapter = adapter

        observeViseModel()
    }


    private fun observeViseModel() {
        val parentFragment = parentFragment as SearchYoutubeFragment
        parentFragment.viewModel.videos.observe(this, Observer {
            if (it.status == Status.SUCCESS) {
                adapter.items = it.data!!
            }
        })
    }

    override fun onItemClick(musicTrack: MusicTrack) {
        val bottomSheetFragment = FvaBottomSheetFragment()
        val bundle = Bundle()
        bundle.putString("MUSIC_TRACK", injector.gson.toJson(musicTrack))
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }
}
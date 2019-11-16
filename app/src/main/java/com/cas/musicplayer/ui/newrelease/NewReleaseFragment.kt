package com.cas.musicplayer.ui.newrelease


import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import com.cas.musicplayer.R
import com.cas.musicplayer.base.common.ResourceOld
import com.cas.musicplayer.base.common.Status
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.ui.BaseFragment
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.cas.musicplayer.utils.Extensions.injector
import com.cas.musicplayer.utils.gone
import com.cas.musicplayer.utils.visible
import com.cas.musicplayer.viewmodel.viewModel
import com.facebook.ads.AdError
import com.facebook.ads.NativeAdsManager
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_new_release.*


class NewReleaseFragment : BaseFragment<NewReleaseViewModel>(), NewReleaseVideoAdapter.OnItemClickListener,
    NativeAdsManager.Listener {

    override val viewModel by viewModel { injector.newReleaseViewModel }
    override val layoutResourceId: Int = R.layout.fragment_new_release
    private lateinit var adapter: NewReleaseVideoAdapter
    private var mNativeAdsManager: NativeAdsManager? = null

    override fun onAdsLoaded() {
        adapter = NewReleaseVideoAdapter(listOf(), context, mNativeAdsManager!!, this) {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.collapseBottomPanel()
        }
        val imgCollapsed = activity?.findViewById<CircleImageView>(R.id.imgCollapsed)

        recyclerView.adapter = adapter
        viewModel.trendingTracks.observe(this, Observer { resource ->
            Picasso.get().load(resource.data?.get(0)?.imgUrl)
                .fit()
                .centerInside()
                .placeholder(R.drawable.bg_circle_black)
                .into(imgCollapsed)
            updateUI(resource)
        })

        viewModel.loadTrendingMusic()
    }

    override fun onAdError(p0: AdError?) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = "New Release"

        val collapsingToolbar =
            activity?.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)

        collapsingToolbar?.isTitleEnabled = true

        collapsingToolbar?.title = "New Release"

        val rltContainer = activity?.findViewById<RelativeLayout>(R.id.rltContainer)


        rltContainer?.visible()

        val placement_id = "261549011432104_261551618098510"
        mNativeAdsManager = NativeAdsManager(activity!!, placement_id, 5)
        mNativeAdsManager!!.loadAds()
        mNativeAdsManager!!.setListener(this)


    }

    private fun updateUI(resource: ResourceOld<List<MusicTrack>>) {
        if (resource.status == Status.LOADING) {
            txtError.gone()
            progressBar.visible()
            recyclerView.gone()
        } else if (resource.status == Status.ERROR) {
            txtError.visible()
            progressBar.gone()
            recyclerView.gone()
        } else {
            txtError.gone()
            progressBar.gone()
            recyclerView.visible()
            adapter.items = resource.data!!
        }
    }

    override fun onItemClick(musicTrack: MusicTrack) {
        val bottomSheetFragment = FvaBottomSheetFragment()
        val bundle = Bundle()
        bundle.putString("MUSIC_TRACK", injector.gson.toJson(musicTrack))
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }
}

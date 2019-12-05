package com.cas.musicplayer.ui.dashboard


import android.os.Bundle
import android.view.View
import com.cas.common.adapter.FragmentPageAdapter
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.home.HomeFragment
import com.cas.musicplayer.ui.library.LibraryFragment
import com.cas.musicplayer.utils.NoViewModelFragment
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : NoViewModelFragment() {

    override val layoutResourceId: Int = R.layout.fragment_dashboard
    override val keepView: Boolean = true

    private val homeFragment by lazy { HomeFragment() }
    private val libraryFragment by lazy { LibraryFragment() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.adapter = FragmentPageAdapter(childFragmentManager, listOf(homeFragment, libraryFragment))
        tabLayout.setupWithViewPager(viewPager)
    }
}

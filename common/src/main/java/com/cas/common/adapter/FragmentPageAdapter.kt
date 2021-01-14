package com.cas.common.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class FragmentPageAdapter(
    fm: FragmentManager,
    private val fragments: List<PageableFragment>,
    private val titles: MutableList<String>
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment = fragments[position] as Fragment
    override fun getCount() = fragments.size
    override fun getPageTitle(position: Int) = titles[position]
}

interface PageableFragment {
    val pageTitle: String
}
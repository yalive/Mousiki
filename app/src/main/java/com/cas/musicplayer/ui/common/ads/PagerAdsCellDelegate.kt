package com.cas.musicplayer.ui.common.ads

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.player.TAG_PAGER


/**
 ***************************************
 * Created by Abdelhadi on 2019-12-01.
 ***************************************
 */
class PagerAdsCellDelegate : AdsCellDelegate() {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_video_pager_ads)
        return AdsViewHolder(view)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        Log.d(TAG_PAGER, "bind ads: $position")
        super.onBindViewHolder(items, position, holder)
    }
}
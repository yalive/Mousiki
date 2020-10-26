package com.cas.musicplayer.ui.common.ads

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.musicplayer.R


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
}
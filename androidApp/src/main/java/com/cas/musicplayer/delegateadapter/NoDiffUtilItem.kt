package com.cas.musicplayer.delegateadapter

import androidx.recyclerview.widget.DiffUtil
import com.mousiki.shared.domain.models.DisplayableItem

class NoDiffUtilItem : DiffUtil.ItemCallback<DisplayableItem>() {
    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return false
    }
}
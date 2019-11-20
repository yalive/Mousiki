package com.cas.musicplayer.ui.searchyoutube

import android.view.View
import com.cas.musicplayer.R
import com.cas.musicplayer.base.SimpleBaseAdapter
import com.cas.musicplayer.base.SimpleBaseViewHolder
import kotlinx.android.synthetic.main.item_youtube_serach_suggestion.view.*


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class YTSearchSuggestionsAdapter(
    private val onClickItem: ((suggestion: String) -> Unit)
) : SimpleBaseAdapter<String, YTSearchSuggestionsViewHolder>() {

    override val cellResId: Int = R.layout.item_youtube_serach_suggestion

    override fun createViewHolder(view: View): YTSearchSuggestionsViewHolder {
        return YTSearchSuggestionsViewHolder(view, onClickItem)
    }
}

class YTSearchSuggestionsViewHolder(
    view: View,
    private val onClickItem: ((suggestion: String) -> Unit)
) : SimpleBaseViewHolder<String>(view) {

    override fun bind(item: String) {
        itemView.txtTitle.text = item
        itemView.setOnClickListener {
            if (adapterPosition >= 0) {
                onClickItem.invoke(item)
            }
        }
    }
}

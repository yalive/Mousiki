package com.cas.musicplayer.ui.searchyoutube

import android.view.View
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.musicplayer.R
import kotlinx.android.synthetic.main.item_youtube_serach_suggestion.view.*


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class SearchSuggestionsAdapter(
    private val onClickItem: ((suggestion: String) -> Unit)
) : SimpleBaseAdapter<String, SearchSuggestionsAdapter.ViewHolder>() {

    override val cellResId: Int = R.layout.item_youtube_serach_suggestion

    override fun createViewHolder(view: View): ViewHolder {
        return ViewHolder(view, onClickItem)
    }

    class ViewHolder(
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
}



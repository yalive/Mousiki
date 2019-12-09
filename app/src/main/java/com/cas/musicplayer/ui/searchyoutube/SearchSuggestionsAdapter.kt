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
    private val onClickItem: ((SearchSuggestion) -> Unit)
) : SimpleBaseAdapter<SearchSuggestion, SearchSuggestionsAdapter.ViewHolder>() {

    override val cellResId: Int = R.layout.item_youtube_serach_suggestion

    override fun createViewHolder(view: View): ViewHolder {
        return ViewHolder(view, onClickItem)
    }

    class ViewHolder(
        view: View,
        private val onClickItem: ((SearchSuggestion) -> Unit)
    ) : SimpleBaseViewHolder<SearchSuggestion>(view) {

        override fun bind(item: SearchSuggestion) {
            itemView.txtTitle.text = item.value
            val searchIcon = if (item.fromHistoric) R.drawable.ic_history else R.drawable.ic_search
            itemView.imgSearch.setImageResource(searchIcon)
            itemView.setOnClickListener {
                if (adapterPosition >= 0) {
                    onClickItem.invoke(item)
                }
            }
        }
    }
}

data class SearchSuggestion(val value: String, val fromHistoric: Boolean = false)



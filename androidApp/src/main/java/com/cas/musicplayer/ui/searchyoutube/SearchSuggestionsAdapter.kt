package com.cas.musicplayer.ui.searchyoutube

import android.view.View
import androidx.core.view.isVisible
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemYoutubeSerachSuggestionBinding
import com.mousiki.shared.ui.search.SearchSuggestion


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class SearchSuggestionsAdapter(
    private val onClickItem: ((SearchSuggestion) -> Unit),
    private val onClickAutocomplete: ((SearchSuggestion) -> Unit),
    private val onClickRemoveHistoricItem: ((SearchSuggestion) -> Unit),
) : SimpleBaseAdapter<SearchSuggestion, SearchSuggestionsAdapter.ViewHolder>() {

    override val cellResId: Int = R.layout.item_youtube_serach_suggestion

    override fun createViewHolder(view: View): ViewHolder {
        val binding = ItemYoutubeSerachSuggestionBinding.bind(view)
        return ViewHolder(binding)
    }

    inner class ViewHolder(
        val binding: ItemYoutubeSerachSuggestionBinding
    ) : SimpleBaseViewHolder<SearchSuggestion>(binding.root) {

        init {
            itemView.onClick {
                val item = dataItems.getOrNull(adapterPosition) ?: return@onClick
                onClickItem.invoke(item)
            }
        }

        override fun bind(item: SearchSuggestion) = with(binding) {
            txtTitle.text = item.value
            val searchIcon = if (item.fromHistoric) R.drawable.ic_history else R.drawable.ic_search
            imgSearch.setImageResource(searchIcon)
            btnPast.onClick {
                if (adapterPosition >= 0) {
                    onClickAutocomplete.invoke(item)
                }
            }
            btnPast.isVisible = !item.fromHistoric
            btnClear.isVisible = item.fromHistoric
            btnClear.onClick {
                onClickRemoveHistoricItem(item)
            }
        }
    }
}



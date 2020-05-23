package com.cas.musicplayer.ui.genres

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.delegatedadapter.AdapterDelegate
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/23/20.
 ***************************************
 */


class HeaderGenreDelegate : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is HeaderGenresItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_list_genres_header)
        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val headerItem = items[position] as HeaderGenresItem
        (holder as HeaderViewHolder).bind(headerItem)
    }

    private inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        fun bind(headerItem: HeaderGenresItem) {
            txtTitle.setText(headerItem.title)
        }
    }
}

data class HeaderGenresItem(@StringRes val title: Int) : DisplayableItem
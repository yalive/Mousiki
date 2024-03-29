package com.cas.musicplayer.ui.home.delegates

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.musicplayer.R
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.cas.musicplayer.ui.home.GridSpacingItemDecoration
import com.cas.musicplayer.ui.home.adapters.HomeGenresAdapter
import com.cas.musicplayer.utils.dpToPixel
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.GenreMusic
import com.mousiki.shared.ui.home.model.HomeItem

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */
class HomeGenreAdapterDelegate : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is HomeItem.GenreItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_home_list_genres)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val genreItem = items[position] as HomeItem.GenreItem
        (holder as GenreViewHolder).bind(genreItem.genres)
    }

    private inner class GenreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var adapter = HomeGenresAdapter()

        init {
            val spacingDp = itemView.context.dpToPixel(8f)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacingDp, true))
            recyclerView.adapter = adapter
        }

        fun bind(genres: List<GenreMusic>) {
            adapter.dataItems = genres.toMutableList()
        }
    }
}
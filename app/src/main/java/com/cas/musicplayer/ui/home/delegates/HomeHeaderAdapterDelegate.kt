package com.cas.musicplayer.ui.home.delegates

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.delegatedadapter.AdapterDelegate
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.HeaderItem

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */
class HomeHeaderAdapterDelegate : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is HeaderItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_home_header)
        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(items: List<DisplayableItem>, position: Int, holder: RecyclerView.ViewHolder) {
        val headerItem = items[position] as HeaderItem
        (holder as HeaderViewHolder).bind(headerItem)
    }

    private inner class HeaderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val showAll: ImageButton = view.findViewById(R.id.showAll)

        fun bind(headerItem: HeaderItem) {
            txtTitle.text = headerItem.title
            view.setOnClickListener { showMore(headerItem) }
            showAll.setOnClickListener { showMore(headerItem) }
        }

        private fun showMore(headerItem: HeaderItem) {
            val destination = when (headerItem) {
                HeaderItem.ArtistsHeader -> R.id.artistsFragment
                HeaderItem.PopularsHeader -> R.id.newReleaseFragment
                HeaderItem.ChartsHeader -> R.id.chartsFragment
                HeaderItem.GenresHeader -> R.id.genresFragment
                HeaderItem.RecentHeader -> R.id.newReleaseFragment
            }
            itemView.findNavController().navigate(destination)
        }
    }
}
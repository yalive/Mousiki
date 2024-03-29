package com.cas.musicplayer.ui.home.delegates

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.cas.musicplayer.utils.AndroidStrings
import com.cas.musicplayer.utils.dpToPixel
import com.cas.musicplayer.utils.navigateSafeAction
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.ui.home.HomeViewModel
import com.mousiki.shared.ui.home.model.HeaderItem
import com.mousiki.shared.ui.home.model.title

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */
class HomeHeaderAdapterDelegate(
    private val viewModel: HomeViewModel
) : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is HeaderItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_home_header)
        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val headerItem = items[position] as HeaderItem
        (holder as HeaderViewHolder).bind(headerItem)
    }

    private inner class HeaderViewHolder(private val view: View) : RecyclerView.ViewHolder(view),
        HomeMarginProvider {
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val txtMore: TextView = view.findViewById(R.id.txtMore)
        private val showAll: ImageButton = view.findViewById(R.id.showAll)
        private val progressBar: ProgressBar = view.findViewById(R.id.progressBar)

        var item: HeaderItem? = null
        fun bind(headerItem: HeaderItem) {
            item = headerItem
            txtTitle.text = headerItem.title(AndroidStrings)
            if (headerItem.showMore) {
                view.onClick { showMore(headerItem) }
                showAll.onClick { showMore(headerItem) }
            }
            if (headerItem is HeaderItem.PopularsHeader) {
                progressBar.isVisible = headerItem.loading
            } else {
                progressBar.isVisible = false
            }

            showAll.isInvisible = !headerItem.showMore
            txtMore.isInvisible = !headerItem.showMore
        }

        private fun showMore(headerItem: HeaderItem) {
            val bundle = Bundle()
            val destination = when (headerItem) {
                HeaderItem.ArtistsHeader -> R.id.action_homeFragment_to_artistsFragment
                is HeaderItem.PopularsHeader -> R.id.action_homeFragment_to_newReleaseFragment
                HeaderItem.ChartsHeader -> R.id.genresFragment // Just for code to compile
                HeaderItem.GenresHeader -> R.id.action_homeFragment_to_genresFragment
            }
            itemView.findNavController().navigateSafeAction(destination, bundle)
        }

        override fun topMargin(): Int {
            val headerItem = item ?: return 0
            val dp = when (headerItem) {
                is HeaderItem.PopularsHeader -> 24f
                HeaderItem.ChartsHeader -> 0f
                HeaderItem.ArtistsHeader -> 56f
                HeaderItem.GenresHeader -> 40f
            }
            return itemView.context.dpToPixel(dp)
        }
    }
}
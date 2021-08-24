package com.cas.musicplayer.ui.home.delegates

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemTopBarBinding
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.cas.musicplayer.ui.settings.SettingsFragment
import com.cas.musicplayer.utils.navigateSafeAction
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.ui.home.model.MousikiTopBarItem

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */
class MousikiTopBarDelegate : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is MousikiTopBarItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_top_bar)
        val binding = ItemTopBarBinding.bind(view)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        (holder as ViewHolder).bind()
    }

    private inner class ViewHolder(
        val binding: ItemTopBarBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {

            binding.btnSettings.onClick { view ->
                val fm = view.findFragment<Fragment>().activity ?: return@onClick
                SettingsFragment.present(fm)
            }

            binding.btnSearch.onClick {
                val navController = it.findNavController()
                navController.navigateSafeAction(R.id.action_homeFragment_to_searchYoutubeFragment)
            }
        }
    }
}
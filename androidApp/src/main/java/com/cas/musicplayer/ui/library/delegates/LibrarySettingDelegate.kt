package com.cas.musicplayer.ui.library.delegates

import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemLibrarySettingsBinding
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.cas.musicplayer.ui.library.adapters.LibrarySettingItem
import com.cas.musicplayer.utils.navigateSafeAction
import com.mousiki.shared.domain.models.DisplayableItem

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */
class LibrarySettingDelegate : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is LibrarySettingItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_library_settings)
        val binding = ItemLibrarySettingsBinding.bind(view)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        (holder as ViewHolder).binding.btnSettings.onClick {
            val nc = it.findNavController()
            nc.navigateSafeAction(R.id.action_libraryFragment_to_settingsFragment)
        }
    }

    private inner class ViewHolder(
        val binding: ItemLibrarySettingsBinding
    ) : RecyclerView.ViewHolder(binding.root)
}
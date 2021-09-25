package com.cas.musicplayer.ui.local.songs.settings

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.cas.common.extensions.onClick
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.LocalSongsSettingsFragmentBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.utils.*
import kotlinx.coroutines.delay

class LocalSongsSettingsFragment : BaseFragment<LocalSongsSettingsViewModel>(
    R.layout.local_songs_settings_fragment
) {
    override val screenName: String = "LocalSongsSettingsFragment"
    override val viewModel by viewModel { Injector.localSongsSettingsViewModel }
    private val binding by viewBinding(LocalSongsSettingsFragmentBinding::bind)

    // For now when user close this fragment we refresh songs list
    // The ideal is to detect if there is any change in the settings
    private var onChangeSettings: (() -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnClickListener {
            // Just to prevent underline click detection
        }
        val adapter = LocalSongsSettingsAdapter()
        binding.recyclerView.adapter = adapter
        binding.btnClose.onClick { activity?.onBackPressed() }

        observe(DeviceInset) { inset ->
            binding.topBar.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topMargin = inset.top
            }
            binding.recyclerView.updatePadding(bottom = inset.bottom)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            val animDuration = resources.getInteger(android.R.integer.config_mediumAnimTime)
            delay(animDuration.toLong())
            observe(viewModel.settingItems.asLiveData()) {
                val items = it ?: return@observe
                binding.progressBar.isVisible = false
                adapter.submitList(items)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        onBackPressCallback {
            isEnabled = false // Disable back press listener
            slideDown()
            onChangeSettings?.invoke()
        }
    }

    companion object {

        fun present(activity: FragmentActivity, onChangeSettings: (() -> Unit)) {
            val fragment = activity.slideUpFragment<LocalSongsSettingsFragment>()
            fragment.onChangeSettings = onChangeSettings
        }
    }
}
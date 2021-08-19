package com.cas.musicplayer.ui.local.songs.settings

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.cas.common.extensions.onClick
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.LocalSongsSettingsFragmentBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.utils.DeviceInset
import com.cas.musicplayer.utils.viewBinding
import kotlinx.coroutines.delay

class LocalSongsSettingsFragment : BaseFragment<LocalSongsSettingsViewModel>(
    R.layout.local_songs_settings_fragment
) {
    override val screenName: String = "LocalSongsSettingsFragment"
    override val viewModel by viewModel { Injector.localSongsSettingsViewModel }
    private val binding by viewBinding(LocalSongsSettingsFragmentBinding::bind)
    private val adapter by lazy { LocalSongsSettingsAdapter() }

    // For now when user close this fragment we refresh songs list
    // The ideal is to detect if there is any change in the settings
    private var onChangeSettings: (() -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
        binding.btnClose.onClick {
            activity?.onBackPressed()
            onChangeSettings?.invoke()
        }

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

        // TODO: detect when user go back using navigation buttons
        // TODO: Use onBackPressedDispatcher instead of handling all back press inside activity
    }

    companion object {

        fun present(activity: AppCompatActivity, onChangeSettings: (() -> Unit)) {
            activity.findViewById<ViewGroup>(R.id.queueFragmentContainer).isVisible = true
            val fragment = activity.supportFragmentManager
                .findFragmentById(R.id.queueFragmentContainer)
                ?: LocalSongsSettingsFragment()
            (fragment as? LocalSongsSettingsFragment)?.onChangeSettings = onChangeSettings
            val fm = activity.supportFragmentManager
            fm.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
                .replace(R.id.queueFragmentContainer, fragment)
                .commit()
        }
    }
}
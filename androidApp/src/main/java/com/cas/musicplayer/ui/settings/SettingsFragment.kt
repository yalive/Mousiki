package com.cas.musicplayer.ui.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import com.cas.common.extensions.onClick
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentSettingsBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.base.adjustStatusBarWithTheme
import com.cas.musicplayer.ui.base.setupToolbar
import com.cas.musicplayer.ui.settings.rate.askUserForFeelingAboutApp
import com.cas.musicplayer.ui.settings.rate.writeFeedback
import com.cas.musicplayer.utils.*
import com.mousiki.shared.preference.UserPrefs

class SettingsFragment : BaseFragment<SettingsViewModel>(
    R.layout.fragment_settings
) {

    override val screenName: String = "SettingsFragment"
    override val viewModel by viewModel { Injector.settingsViewModel }
    private val binding by viewBinding(FragmentSettingsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(DeviceInset) { inset ->
            binding.toolbarView.toolbar.updatePadding(top = inset.top)
        }
        setupToolbar(binding.toolbarView.toolbar, R.string.menu_settings) {
            activity?.onBackPressed()
        }
        adjustStatusBarWithTheme()
        binding.btnDarkMode.onClick {
            AlertDialog.Builder(requireContext(), R.style.AppTheme_AlertDialog)
                .setSingleChoiceItems(R.array.dark_mode_values, UserPrefs.getThemeModeValue(), null)
                .setTitle(R.string.choose_dark_mode_theme)
                .setNegativeButton(R.string.cancel, { dialog, whichButton -> })
                .setPositiveButton(
                    R.string.ok
                ) { dialog, whichButton ->
                    dialog.dismiss()
                    val selectedPosition: Int =
                        (dialog as AlertDialog).getListView().getCheckedItemPosition()

                    if (selectedPosition == UserPrefs.THEME_AUTOMATIC) {
                        UserPrefs.setThemeModeValue(UserPrefs.THEME_AUTOMATIC)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    } else if (selectedPosition == UserPrefs.THEME_LIGHT) {
                        UserPrefs.setThemeModeValue(UserPrefs.THEME_LIGHT)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    } else if (selectedPosition == UserPrefs.THEME_DARK) {
                        UserPrefs.setThemeModeValue(UserPrefs.THEME_DARK)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                }
                .show()
        }

        binding.vSwitchPip.isVisible = SystemSettings.isPiPSupported()

        binding.btnTimer.onClick {
            TimerDialog.present(childFragmentManager)
        }

        binding.btnFeedback.onClick {
            context?.writeFeedback()
        }
        binding.btnLikeFacebook.onClick {
            Utils.openFacebookPage(requireContext())
        }
        binding.btnPolicy.onClick {
            Utils.openWebview(requireContext(), "file:///android_asset/policy.html")
        }

        binding.btnRateApp.onClick {
            context?.askUserForFeelingAboutApp()
        }
        binding.btnShareApp.onClick {
            Utils.shareAppVia()
        }
        binding.btnSwitchPip.onClick {
            SystemSettings.openPipSetting(requireActivity())
        }
        binding.btnOutVideoSize.isVisible = !SystemSettings.isPiPSupported()
        binding.dividerVideoSize.isVisible = !SystemSettings.isPiPSupported()
        binding.btnOutVideoSize.onClick {
            AlertDialog.Builder(requireContext(), R.style.AppTheme_AlertDialog)
                .setSingleChoiceItems(
                    R.array.out_video_size_values,
                    UserPrefs.getOutVideoSizeValue(),
                    null
                )
                .setTitle(R.string.settings_choose_out_video_size)
                .setNegativeButton(R.string.cancel, { dialog, whichButton -> })
                .setPositiveButton(
                    R.string.ok
                ) { dialog, whichButton ->
                    dialog.dismiss()
                    val selectedPosition: Int =
                        (dialog as AlertDialog).getListView().getCheckedItemPosition()
                    val size = when (selectedPosition) {
                        0 -> UserPrefs.OutVideoSize.SMALL
                        1 -> UserPrefs.OutVideoSize.NORMAL
                        2 -> UserPrefs.OutVideoSize.LARGE
                        3 -> UserPrefs.OutVideoSize.CIRCULAR
                        else -> UserPrefs.OutVideoSize.CIRCULAR
                    }
                    UserPrefs.setOutVideoSize(size)
                }
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        onBackPressCallback {
            isEnabled = false // Disable back press listener
            slideDown()
        }
        binding.switchPip.isChecked = SystemSettings.canEnterPiPMode()
    }

    companion object {

        fun present(activity: FragmentActivity) {
            activity.slideUpFragment<SettingsFragment>()
        }
    }
}

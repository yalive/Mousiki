package com.cas.musicplayer.ui.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import com.cas.common.extensions.onClick
import com.cas.common.fragment.BaseFragment
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.ui.settings.rate.askUserForFeelingAboutApp
import com.cas.musicplayer.ui.settings.rate.writeFeedback
import com.cas.musicplayer.utils.UserPrefs
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.navigateSafeAction
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : BaseFragment<SettingsViewModel>() {

    override val layoutResourceId: Int = R.layout.fragment_settings
    override val viewModel by viewModel { injector.settingsViewModel }
    override val screenTitle: String by lazy {
        getString(R.string.menu_settings)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adjustStatusBarWithTheme()
        btnDarkMode.onClick {
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
        btnTimer.onClick {
            findNavController().navigateSafeAction(R.id.action_settingsFragment_to_timerDialog)
        }
        btnFeedback.onClick {
            context?.writeFeedback()
        }
        btnLikeFacebook.onClick {
            Utils.openFacebookPage(requireContext())
        }
        btnPolicy.onClick {
            Utils.openWebview(requireContext(), "file:///android_asset/policy.html")
        }

        btnRateApp.onClick {
            context?.askUserForFeelingAboutApp()
        }
        btnShareApp.onClick {
            Utils.shareAppVia()
        }

        btnOutVideoSize.onClick {
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
}

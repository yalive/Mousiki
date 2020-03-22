package com.cas.musicplayer.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.cas.common.extensions.onClick
import com.cas.common.fragment.BaseFragment
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.utils.Utils
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment<SettingsViewModel>() {

    override val layoutResourceId: Int = R.layout.fragment_settings
    override val viewModel by viewModel { injector.settingsViewModel }
    override val screenTitle: String by lazy {
        getString(R.string.menu_settings)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnTimer.onClick {
            findNavController().navigate(R.id.action_settingsFragment_to_timerDialog)
        }
        btnFeedback.onClick {
            Utils.sendEmail(requireContext())
        }
        btnLikeFacebook.onClick {
            val urlFacebookPage: Uri = Uri.parse("https://www.facebook.com/mousiki2")
            val intent = Intent(Intent.ACTION_VIEW, urlFacebookPage)
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            }
        }
        btnPolicy.onClick {
            Utils.openWebview(requireContext(), "file:///android_asset/policy.html")
        }

        btnRateApp.onClick {
            Utils.rateApp(requireContext())
        }
        btnShareApp.onClick {
            Utils.shareAppVia()
        }
    }
}

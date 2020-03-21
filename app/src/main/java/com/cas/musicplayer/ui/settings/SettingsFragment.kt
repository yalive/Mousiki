package com.cas.musicplayer.ui.settings

import android.os.Bundle
import android.view.View
import com.cas.common.fragment.BaseFragment
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment<SettingsViewModel>() {

    override val layoutResourceId: Int = R.layout.fragment_settings
    override val viewModel by viewModel { injector.settingsViewModel }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnTimer.setOnClickListener {
            TimerDialog().show(childFragmentManager, "")
        }
    }
}

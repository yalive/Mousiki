package com.cas.musicplayer.utils

import androidx.annotation.LayoutRes
import com.cas.musicplayer.ui.base.BaseFragment
import com.mousiki.shared.ui.base.BaseViewModel
import com.cas.common.viewmodel.activityViewModel
import com.cas.musicplayer.di.Injector

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 *
 *********************************************
 */
abstract class NoViewModelFragment(
    @LayoutRes layoutResourceId: Int
) : BaseFragment<EmptyViewModel>(layoutResourceId) {
    override val viewModel by activityViewModel { Injector.emptyViewModel }
}

class EmptyViewModel : BaseViewModel()
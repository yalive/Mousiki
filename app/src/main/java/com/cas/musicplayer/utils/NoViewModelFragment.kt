package com.cas.musicplayer.utils

import com.cas.common.viewmodel.BaseViewModel
import com.cas.common.fragment.BaseFragment
import com.cas.musicplayer.di.injector.injector
import com.cas.common.viewmodel.activityViewModel
import javax.inject.Inject

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 * Copyright © BDSI group BNP Paribas 2019
 *********************************************
 */
abstract class NoViewModelFragment : BaseFragment<EmptyViewModel>() {
    override val viewModel by activityViewModel { injector.emptyViewModel }
}

class EmptyViewModel @Inject constructor() : BaseViewModel()
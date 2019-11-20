package com.cas.musicplayer.base

import com.cas.musicplayer.ui.BaseFragment
import com.cas.musicplayer.utils.Extensions.injector
import com.cas.musicplayer.viewmodel.activityViewModel
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
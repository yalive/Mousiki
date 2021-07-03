package com.cas.musicplayer.ui.base

import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.cas.common.extensions.isDarkMode
import com.cas.musicplayer.R
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.utils.themeColor
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.utils.AnalyticsApi
import com.mousiki.shared.utils.resolve
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-10.
 ***************************************
 */
abstract class BaseFragment<T : BaseViewModel>(
    @LayoutRes layoutResourceId: Int
) : Fragment(layoutResourceId), KoinComponent {

    protected abstract val viewModel: T
    protected open val screenTitle = ""

    private val analyticsApi by lazy { get<AnalyticsApi>() }

    override fun onResume() {
        super.onResume()
        observe(viewModel.toast.asLiveData()) {
            it?.getContentIfNotHandled()?.let { value ->
                val message = requireContext().resolve(value)
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }
        analyticsApi.logScreenView(javaClass.simpleName)
    }
}


fun Fragment.adjustStatusBarWithTheme() {
    if (requireContext().isDarkMode()) {
        requireActivity().window.statusBarColor =
            requireContext().themeColor(R.attr.colorSurface)
        darkStatusBar()
    } else {
        requireActivity().window.statusBarColor = Color.WHITE
        lightStatusBar()
    }
}

fun Fragment.lightStatusBar() {
    val window = requireActivity().window
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val flags = window.decorView.systemUiVisibility
        window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}

fun Fragment.darkStatusBar() {
    val window = requireActivity().window
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val flags = window.decorView.systemUiVisibility
        window.decorView.systemUiVisibility =
            flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }
}

fun Fragment.setupToolbar(
    toolbar: Toolbar,
    @StringRes title: Int,
    onBack: () -> Unit = { findNavController().popBackStack() }
) {
    toolbar.setTitle(title)
    toolbar.setNavigationOnClickListener { onBack() }
}

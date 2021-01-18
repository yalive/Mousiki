package com.cas.common.fragment

import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cas.common.extensions.isDarkMode
import com.cas.common.extensions.observeEvent
import com.cas.common.viewmodel.BaseViewModel

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-10.
 ***************************************
 */
abstract class BaseFragment<T : BaseViewModel>(
    @LayoutRes layoutResourceId: Int
) : Fragment(layoutResourceId) {

    protected abstract val viewModel: T
    protected open val screenTitle = ""

    override fun onResume() {
        super.onResume()
        setupToolbar()
        observeEvent(viewModel.toast) {
            Toast.makeText(requireContext(), it.resMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupToolbar() {
        val compatActivity = activity as? AppCompatActivity ?: return
        if (withToolbar()) {
            compatActivity.title = screenTitle
            compatActivity.supportActionBar?.show()
        } else {
            compatActivity.supportActionBar?.hide()
        }
    }

    open fun withToolbar(): Boolean = true

    fun lightStatusBar() {
        val window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val flags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun darkStatusBar() {
        val window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val flags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility =
                flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }

    fun adjustStatusBarWithTheme() {
        if (requireContext().isDarkMode()) {
            requireActivity().window.statusBarColor = Color.BLACK
            darkStatusBar()
        } else {
            requireActivity().window.statusBarColor = Color.WHITE
            lightStatusBar()
        }
    }
}
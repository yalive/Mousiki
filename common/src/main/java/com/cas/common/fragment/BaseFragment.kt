package com.cas.common.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cas.common.R
import com.cas.common.viewmodel.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-10.
 ***************************************
 */
abstract class BaseFragment<T : BaseViewModel> : Fragment(), CoroutineScope {

    protected abstract val viewModel: T
    @get:LayoutRes
    protected abstract val layoutResourceId: Int
    protected open val keepView = false
    protected open val screenTitle by lazy {
        getString(R.string.app_name)
    }

    private val job = Job()
    override val coroutineContext = job + Dispatchers.Main

    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (keepView && ::rootView.isInitialized) {
            return rootView
        }
        val view = inflater.inflate(layoutResourceId, container, false)
        if (keepView && view != null) {
            rootView = view
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (withToolbar()) {
            activity?.title = screenTitle
        }
        val compatActivity = activity as? AppCompatActivity
        if (withToolbar()) {
            compatActivity?.supportActionBar?.show()
        } else {
            compatActivity?.supportActionBar?.hide()
        }
    }

    open fun withToolbar(): Boolean = true

    override fun onStop() {
        super.onStop()
        job.cancel()
    }

    fun lightStatusBar() {
        val window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val flags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            // window.statusBarColor = requireContext().color(android.color.)
        }
    }

    fun darkStatusBar() {
        val window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val flags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility =
                flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        } else {
            // window.statusBarColor = color(R.color.colorPrimary)
        }
    }
}
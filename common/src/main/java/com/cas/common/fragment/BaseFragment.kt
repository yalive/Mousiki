package com.cas.common.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
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


    override fun onStop() {
        super.onStop()
        job.cancel()
    }
}
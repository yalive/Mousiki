package com.mousiki.shared.ui.base

import com.mousiki.shared.ui.event.Event
import com.mousiki.shared.utils.TextResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

/**
 **********************************
 * Created by Abdelhadi on 2019-05-28.
 **********************************
 */
expect open class BaseViewModel {

    val scope: CoroutineScope

    val toast: StateFlow<Event<TextResource>?>

    fun clearScope()

    fun showToast(message: String)
}
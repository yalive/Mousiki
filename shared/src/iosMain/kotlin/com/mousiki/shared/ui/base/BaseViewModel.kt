package com.mousiki.shared.ui.base

import com.mousiki.shared.ui.event.Event
import com.mousiki.shared.ui.event.asEvent
import com.mousiki.shared.utils.TextResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual open class BaseViewModel {
    private val mainScope = MainScope()
    actual val scope: CoroutineScope
        get() = mainScope

    private val _toast = MutableStateFlow<Event<TextResource>?>(null)
    actual val toast: StateFlow<Event<TextResource>?> = _toast

    actual fun showToast(message: String) {
        _toast.value = TextResource.fromText(message).asEvent()
    }

    actual fun clearScope() {
        scope.cancel()
    }
}
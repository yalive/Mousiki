package com.mousiki.shared.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mousiki.shared.ui.event.Event
import com.mousiki.shared.ui.event.asEvent
import com.mousiki.shared.utils.TextResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual open class BaseViewModel : ViewModel() {

    actual val scope: CoroutineScope
        get() = viewModelScope

    private val _toast = MutableStateFlow<Event<TextResource>?>(null)
    actual val toast: StateFlow<Event<TextResource>?> = _toast

    actual fun showToast(message: String) {
        _toast.value = TextResource.fromText(message).asEvent()
    }

    override fun onCleared() {
        super.onCleared()
        clearScope()
    }

    actual fun clearScope() {
        // Not needed for android: viewModelScope is automatically cleared
    }
}
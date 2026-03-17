package com.denica.playlistmaker.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <T> throttleFirst(
    windowMillis: Long,
    coroutineScope: CoroutineScope,
    action: (T) -> Unit
): (T) -> Unit {
    var throttleJob: Job? = null
    return throttled@{ param: T ->
        if (throttleJob?.isActive == true) return@throttled
        action(param)
        throttleJob = coroutineScope.launch { delay(windowMillis) }
    }
}

fun throttleFirst(
    windowMillis: Long,
    coroutineScope: CoroutineScope,
    action: () -> Unit
): () -> Unit {
    var throttleJob: Job? = null
    return throttled@{
        if (throttleJob?.isActive == true) return@throttled
        action()
        throttleJob = coroutineScope.launch { delay(windowMillis) }
    }
}

fun <T> debounce(
    delayMillis: Long,
    coroutineScope: CoroutineScope,
    useLastParam: Boolean,
    action: (T) -> Unit
): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        if (useLastParam) {
            debounceJob?.cancel()
        }
        if (debounceJob?.isCompleted != false || useLastParam) {
            debounceJob = coroutineScope.launch {
                delay(delayMillis)
                action(param)
            }
        }
    }
}

fun debounce(
    delayMillis: Long,
    coroutineScope: CoroutineScope,
    useLastParam: Boolean,
    action: () -> Unit
): () -> Unit {
    var debounceJob: Job? = null
    return {
        if (useLastParam) {
            debounceJob?.cancel()
        }
        if (debounceJob?.isCompleted != false || useLastParam) {
            debounceJob = coroutineScope.launch {
                delay(delayMillis)
                action()
            }
        }
    }
}

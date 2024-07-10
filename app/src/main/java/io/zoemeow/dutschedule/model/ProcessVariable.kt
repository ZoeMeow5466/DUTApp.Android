package io.zoemeow.dutschedule.model

import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import io.zoemeow.dutschedule.GlobalVariables
import io.zoemeow.dutschedule.utils.launchOnScope

data class ProcessVariable<T>(
    val processState: MutableState<ProcessState> = mutableStateOf(ProcessState.NotRunYet),
    val lastRequest: MutableLongState = mutableLongStateOf(0),
    val data: MutableState<T?> = mutableStateOf(null),
    val onBeforeRefresh: (() -> Unit)? = null,
    val onRefresh: (T?, Map<String, String>?) -> T?,
    val onAfterRefresh: ((Boolean) -> Unit)? = null
) {
    private fun isExpired(): Boolean {
        return (lastRequest.longValue + GlobalVariables.REQUEST_EXPIRED_DURATION) < System.currentTimeMillis()
    }

    private fun isSuccessfulRequestExpired(): Boolean {
        return when (processState.value) {
            ProcessState.Successful -> isExpired()
            else -> true
        }
    }

    fun resetToDefault() {
        if (processState.value != ProcessState.Running) {
            processState.value = ProcessState.NotRunYet
            lastRequest.longValue = 0
            data.value = null
        }
    }

    fun refreshData(
        args: Map<String, String>? = null,
        force: Boolean = false,
        after: ((Boolean) -> Unit)? = null
    ) {
        if (!isSuccessfulRequestExpired() && !force) {
            after?.let { it(true) }
            onAfterRefresh?.let { it(true) }
            return
        }
        onBeforeRefresh?.let { it() }
        processState.value = ProcessState.Running

        launchOnScope(
            script = {
                val data1 = onRefresh(data.value, args)
                if (data1 != null) {
                    data.value = data1
                }
            },
            invokeOnCompleted = { throwable ->
                throwable?.printStackTrace()
                lastRequest.longValue = System.currentTimeMillis()
                processState.value = if (throwable != null) ProcessState.Failed else ProcessState.Successful
                after?.let { it(throwable == null) }
                onAfterRefresh?.let { it(throwable == null) }
            }
        )
    }
}

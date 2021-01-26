package jp.co.soramitsu.common.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

inline fun <T, R> Flow<List<T>>.mapList(crossinline mapper: suspend (T) -> R) = map { list ->
    list.map { item -> mapper(item) }
}

fun <T> Flow<T>.asLiveData(scope: CoroutineScope): LiveData<T> {
    val liveData = MutableLiveData<T>()

    flowOn(Dispatchers.Default)
        .onEach {
            liveData.value = it
        }.onCompletion { Log.d("RX", "Cancelled") }.launchIn(scope)

    return liveData
}
package com.ojhdtapp.action.logic.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ojhdtapp.action.logic.Repository
import kotlinx.coroutines.*

class AutoSuggestWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(
    context,
    workerParams
) {
    override suspend fun doWork(): Result {
        val result = withContext(Dispatchers.Default) {
            Repository.storeSuggestFromCloud(1)
        }
        Log.d("aaa", result.isSuccess.toString())
        return if (result.isSuccess) Result.success() else Result.failure()
    }
}
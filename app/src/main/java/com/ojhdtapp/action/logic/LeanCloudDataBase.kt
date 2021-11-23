package com.ojhdtapp.action.logic

import cn.leancloud.LCObject
import io.reactivex.disposables.Disposable
import cn.leancloud.LCQuery
import io.reactivex.Observer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object LeanCloudDataBase {
    suspend fun getNewSuggest(type: Int) = suspendCoroutine<LCObject> {
        val query = LCQuery<LCObject>("Suggest")
        when(type){
            1 -> query.whereEqualTo("type", 1)
            2 -> query.whereEqualTo("type", 2)
            else -> {}
        }
        query.firstInBackground.subscribe(object : Observer<LCObject?> {
            override fun onSubscribe(d: Disposable) {
                TODO("Not yet implemented")
            }

            override fun onNext(t: LCObject) {
                it.resume(t)
            }

            override fun onError(e: Throwable) {
                it.resumeWithException(e)
            }

            override fun onComplete() {
                TODO("Not yet implemented")
            }
        })
    }
}
package com.ojhdtapp.action.logic

import cn.leancloud.LCObject
import io.reactivex.disposables.Disposable
import cn.leancloud.LCQuery
import com.ojhdtapp.action.logic.model.Suggest
import io.reactivex.Observer
import java.sql.ClientInfoStatus
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object LeanCloudDataBase {
    val dataBase by lazy {
        AppDataBase.getDataBase()
    }

    fun lcObject2Suggest(
        obj: LCObject,
        votingStatus: Int = 0,
        archived: Boolean = false,
        read: Boolean = false
    ): Suggest {
        val list = obj.getList("label") as List<HashMap<String, Any>>
        val map = mutableMapOf<Int, String>()
        list.forEach {
            map[it["first"] as Int] = it["second"] as String
        }
        return Suggest(
            obj.getString("title"),
            obj.getString("subhead"),
            obj.getString("imgUrl"),
            obj.getDate("time"),
            obj.getString("authorAvatarUrl"),
            obj.getString("author"),
            obj.getString("source"),
            obj.getInt("type"),
            obj.getString("content"),
            map,
            obj.getString("sourceUrl"),
            obj.getInt("like"),
            obj.getInt("dislike"),
            votingStatus,
            archived,
            read,
            obj.objectId
        )
    }

    suspend fun getNewSuggest(type: Int) = suspendCoroutine<Suggest> {
        val query = LCQuery<LCObject>("Suggest")
        when (type) {
            1 -> query.whereEqualTo("type", 1)
            2 -> query.whereEqualTo("type", 2)
            else -> {}
        }
        query.firstInBackground.subscribe(object : Observer<LCObject?> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: LCObject) {
                it.resume(lcObject2Suggest(t))
            }

            override fun onError(e: Throwable) {
                it.resumeWithException(e)
            }

            override fun onComplete() {
            }
        })
    }

    suspend fun syncSuggest(objectId: String): Suggest{
        val result = suspendCoroutine<Suggest> {
            val storedSuggest = dataBase.suggestDao().querySuggestByObjId(objectId)
            val query = LCQuery<LCObject>("Suggest")
            query.getInBackground(objectId).subscribe(object : Observer<LCObject> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: LCObject) {
                    it.resume(
                        lcObject2Suggest(
                            t, storedSuggest.votingStatus,
                            storedSuggest.archived,
                            storedSuggest.read
                        )
                    )
                }

                override fun onError(e: Throwable) {
                    it.resumeWithException(e)
                }

                override fun onComplete() {
                }
            })
        }
        dataBase.suggestDao().updateSuggest(result)
        return result
    }
}
package com.ojhdtapp.action.logic

import android.util.Log
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
    private val dataBase by lazy {
        AppDataBase.getDataBase()
    }

    fun lcObject2Suggest(
        obj: LCObject,
        suggest: Suggest? = null,
        vtStatus: Int = 0,
        acd: Boolean = false,
        rd: Boolean = false,
        ded: Boolean = false,
    ): Suggest {
        val storedSuggest = suggest ?: Suggest()
        val list = obj.getList("label") as List<HashMap<String, Any>>
        val map = mutableMapOf<Int, String>()
        list.forEach {
            map[it["first"] as Int] = it["second"] as String
        }
        return storedSuggest.apply {
            title = obj.getString("title")
            subhead = obj.getString("subhead")
            imgUrl = obj.getString("imgUrl")
            time = obj.getDate("time")
            authorAvatarUrl = obj.getString("authorAvatarUrl")
            author = obj.getString("author")
            source = obj.getString("cause")
            type = obj.getInt("type")
            content = obj.getString("content")
            label = map
            sourceUrl = obj.getString("sourceUrl")
            like = obj.getInt("like")
            dislike = obj.getInt("dislike")
            votingStatus = vtStatus
            archived = acd
            read = rd
            deleted = ded
            objectId = obj.objectId
        }
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

    suspend fun syncSuggest(objectId: String): Suggest {
        val result = suspendCoroutine<Suggest> {
            val storedSuggest = dataBase.suggestDao().querySuggestByObjId(objectId)
            val query = LCQuery<LCObject>("Suggest")
            query.getInBackground(objectId).subscribe(object : Observer<LCObject> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: LCObject) {
                    val result = lcObject2Suggest(
                        t, storedSuggest, storedSuggest.votingStatus,
                        storedSuggest.archived,
                        storedSuggest.read,
                        storedSuggest.deleted
                    )
                    it.resume(result)
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
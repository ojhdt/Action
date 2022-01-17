package com.ojhdtapp.action.logic

import android.util.Log
import cn.leancloud.LCObject
import io.reactivex.disposables.Disposable
import cn.leancloud.LCQuery
import com.ojhdtapp.action.logic.model.Action
import com.ojhdtapp.action.logic.model.Suggest
import io.reactivex.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
            source = obj.getString("source")
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

    fun lcObject2Action(obj:LCObject) : Action{
        val storeAction = Action()
        val list = obj.getList("label") as List<HashMap<String, Any>>
        val map = mutableMapOf<Int, String>()
        list.forEach {
            map[it["first"] as Int] = it["second"] as String
        }
        return storeAction.apply {
            title = obj.getString("title")
            imageID = obj.getInt("imageID")
            content = obj.getString("content")
            label = map
            hightlight = obj.getList("highlight") as List<String>
            weight = obj.getInt("weight")
            activityStateTrigger = obj.getInt("activityStateTrigger")
            lightStateTrigger = obj.getInt("lightStateTrigger")
            locationStateTrigger = obj.getInt("locationStateTrigger")
            timeStateTrigger = obj.getInt("timeStateTrigger")
            weatherStateTrigger = obj.getInt("weatherStateTrigger")
            canSaveWater = obj.getInt("canSaveWater").toFloat() / 1000
            canSaveElectricity = obj.getInt("canSaveElectricity").toFloat() / 1000
            canSaveTree = obj.getInt("canSaveTree").toFloat() / 1000
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

    fun syncAllAction(){
        val query = LCQuery<LCObject>("Action")
        query.findInBackground().subscribe(object : Observer<List<LCObject>> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: List<LCObject>) {
                storeActionsToDatabase(t)
            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {
            }


        })
    }

    fun storeActionsToDatabase(list:List<LCObject>){
        val job = Job()
        CoroutineScope(job).launch {
            list.forEach {
                if(!dataBase.actionDao().isStored(it.objectId))
                    dataBase.actionDao().insertAction(lcObject2Action(it))
            }
        }
        job.complete()
    }
}
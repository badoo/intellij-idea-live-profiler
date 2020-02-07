package com.badoo.liveprof.plugin

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.RoamingType
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.util.xmlb.XmlSerializerUtil
import java.util.concurrent.ConcurrentHashMap


@State(name = "LiveProfMethodsStorage", storages = [com.intellij.openapi.components.Storage("LiveProfStorage.xml", roamingType = RoamingType.DISABLED)])
class LiveProfStorage : PersistentStateComponent<LiveProfStorage> {

    var liveprofMethods: ConcurrentHashMap<String, String>? = null

    override fun loadState(state: LiveProfStorage) = XmlSerializerUtil.copyBean(state, this)

    override fun getState(): LiveProfStorage? = this

    companion object {
        @JvmStatic
        fun getInstance(): LiveProfStorage {
            return ServiceManager.getService(LiveProfStorage::class.java)
        }
    }

    fun update(map: Map<String, String>) {
        if (liveprofMethods != null) {
            liveprofMethods!!.clear()
        } else {
            liveprofMethods = ConcurrentHashMap(map.size)
        }

        for ((method, date) in map) {
            val fqn = "\\" + method.replace("::", ".")
            liveprofMethods!![fqn] = date
        }
    }

    fun get(fqn : String) : String? {
        if (liveprofMethods == null) {
            return null
        }
        if (!liveprofMethods!!.containsKey(fqn)) {
            return null;
        }

        return liveprofMethods!!.get(fqn)
    }
}
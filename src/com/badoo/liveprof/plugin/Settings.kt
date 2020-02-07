package com.badoo.liveprof.plugin

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.RoamingType
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Tag

@State(name = "Settings", storages = [com.intellij.openapi.components.Storage("liveProfSettings.xml", roamingType = RoamingType.DEFAULT)])
class Settings : PersistentStateComponent<Settings> {

    @Tag("Urls")
    data class UrlsDescriptor(
            @JvmField
            @Tag("getLiveProfMethods")
            var getLiveProfMethods: String? = "http://liveprof.org/profiler/all-methods.json?uuid=70366397-97d6-41be-a83c-e9e649c824e1&",

            @JvmField
            @Tag("getLiveProfMethodInfo")
            var getLiveProfMethodInfo: String? = "http://liveprof.org/profiler/method-used-apps.json?uuid=70366397-97d6-41be-a83c-e9e649c824e1&method=",

            @JvmField
            @Tag("getLiveProfUrl")
            var getLiveProfUrl: String? = "http://liveprof.org/profiler/method-usage.phtml?uuid=70366397-97d6-41be-a83c-e9e649c824e1&method=",

            @JvmField
            @Tag("getLiveProfGraphsUrl")
            var getLiveProfGraphsUrl: String? = "http://liveprof.org/profiler/tree-view.phtml?uuid=70366397-97d6-41be-a83c-e9e649c824e1&"
    )

    @Tag("enableDebug")
    @JvmField var enableDebug : Boolean? = false

    @Tag("refreshLiveProfMethodsSecs")
    @JvmField var refreshLiveProfMethodsSecs : Int? = 60 * 60 * 4

    @Tag("enableLiveProfiler")
    @JvmField var enableLiveProfiler : Boolean? = true

    @Tag("enableLiveProfilerMethodInfo")
    @JvmField var enableLiveProfilerMethodInfo : Boolean? = true

    // TODO: kill me after 2020-05-31
    // left for backward compatibility
    @JvmField var Urls: UrlsDescriptor? = null
    @JvmField var urls: UrlsDescriptor? = UrlsDescriptor()

    override fun loadState(p0: Settings) {
        XmlSerializerUtil.copyBean(p0, this);

        // backward compatibility
        if (this.Urls != null) {
            this.urls = this.Urls
            this.Urls = null
        }
    }

    override fun getState(): Settings {
        return this
    }

    override fun noStateLoaded() {
        debug("Settings: noStateLoaded")
    }

    @Suppress("SameParameterValue")
    private fun debug(string: String) {
        // can't use Notification.debug in this class, as it will cause a stack overflow
        if (enableDebug != null && enableDebug!!) {
            Notification.info(string)
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(): Settings {
            return ServiceManager.getService(Settings::class.java)
        }
    }
}
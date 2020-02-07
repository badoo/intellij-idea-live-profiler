package com.badoo.liveprof.plugin

import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.MapAnnotation
import com.intellij.util.xmlb.annotations.Property

@Suppress("PropertyName")
data class MethodInfo(
        @Attribute("app")
        @JvmField val app : String? = null,

        @Attribute("label")
        @JvmField val label : String? = null,

        @Attribute("date")
        @JvmField val date : String? = null,

        @Attribute("cpu")
        @JvmField val cpu : Float? = null,

        @Attribute("ct")
        @JvmField val ct : Float? = null,

        @Attribute("calls_count")
        @JvmField val calls_count : Int? = null
)

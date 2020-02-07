package com.badoo.liveprof.plugin.ui

interface SettingHolder {
    fun isChanged() : Boolean
    fun commit()
    fun reset()
}
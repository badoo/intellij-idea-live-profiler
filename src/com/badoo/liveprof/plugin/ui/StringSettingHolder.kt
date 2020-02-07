package com.badoo.liveprof.plugin.ui

import javax.swing.JTextField
import java.lang.reflect.Field

class StringSettingHolder(private val param: Field, private val obj: Any, private val editor: JTextField) : SettingHolder {
    private var initialValue = param.get(obj) as? String ?: ""

    init {
        editor.text = param.get(obj) as? String ?: ""
    }

    override fun isChanged(): Boolean {
        return editor.text != initialValue
    }

    override fun commit() {
        if (isChanged()) {
            param.set(obj, editor.text)
            initialValue = editor.text
        }
    }

    override fun reset() {
        editor.text = initialValue
    }
}
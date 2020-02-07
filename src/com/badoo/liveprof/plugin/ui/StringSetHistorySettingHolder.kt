package com.badoo.liveprof.plugin.ui

import com.intellij.ui.TextFieldWithHistory
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton
import java.lang.reflect.Field
import javax.swing.JComboBox


class StringSetHistorySettingHolder(private val param: Field, private val obj: Any, private val editor: TextFieldWithHistoryWithBrowseButton) : SettingHolder {
    private var initialValue = param.get(obj) as String

    init {
        val value = param.get(obj) ?: ""
        if (value != "") {
            editor.text = value as String
        }
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
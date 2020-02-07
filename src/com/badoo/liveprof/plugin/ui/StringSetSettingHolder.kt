package com.badoo.liveprof.plugin.ui

import java.lang.reflect.Field
import javax.swing.JComboBox


class StringSetSettingHolder(private val param: Field, private val obj: Any, private val editor: JComboBox<Any>) : SettingHolder {
    private var initialValue = param.get(obj) as String

    init {
        val value = param.get(obj) as String
        if (value != "") {
            if (!hasItem(value)) {
                editor.addItem(value)
            }

            editor.selectedItem = value
        }
    }

    override fun isChanged(): Boolean {
        return editor.selectedItem != initialValue
    }

    override fun commit() {
        if (isChanged()) {
            param.set(obj, editor.selectedItem)
            initialValue = editor.selectedItem as String
        }
    }

    private fun hasItem(string: String) : Boolean
    {
        for (i in 0 until editor.itemCount) {
            if (editor.getItemAt(i) == string) {
                return true
            }
        }

        return false
    }

    override fun reset() {
        editor.selectedItem = initialValue
    }
}
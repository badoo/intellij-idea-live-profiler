package com.badoo.liveprof.plugin.ui

import java.lang.reflect.Field
import javax.swing.JCheckBox
import javax.swing.JComboBox


class BooleanSettingHolder(private val param: Field, private val obj: Any, private val editor: JCheckBox) : SettingHolder {
    private var initialValue = param.get(obj) as? Boolean ?: false

    init {
        val value = param.get(obj) as? Boolean ?: false
        editor.isSelected = value
    }

    override fun isChanged(): Boolean {
        return editor.isSelected != initialValue
    }

    override fun commit() {
        if (isChanged()) {
            param.set(obj, editor.isSelected.to(Boolean::class.java).first)
        }
    }

    override fun reset() {
        editor.isSelected = initialValue
    }
}
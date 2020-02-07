package com.badoo.liveprof.plugin.ui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

abstract class UpdateListener implements DocumentListener {

    UpdateListener() {
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        changed(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        changed(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        changed(e);
    }

    abstract void changed(DocumentEvent e);
}

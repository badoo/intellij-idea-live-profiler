package com.badoo.liveprof.plugin.ui;

import com.badoo.liveprof.plugin.Notification;
import com.badoo.liveprof.plugin.Settings;
import com.badoo.liveprof.plugin.LiveProfLoader;
import com.badoo.liveprof.plugin.LiveProfStorage;
import com.badoo.liveprof.plugin.MethodsMap;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.ui.components.JBTextField;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jetbrains.annotations.Nullable;
import java.io.File;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.*;

/**
 * This class is in Java as IDEA CE can not link Kotlin files and GUI-editor
 */
public class SettingsForm implements Configurable {
    private JPanel mainPanel;
    private JCheckBox chkEnableDebug;

    private JComboBox sldLiveProfMethodsUpdateInterval;
    private JButton btnUpdateLiveProfMethodsNow;
    private JTextField txtGetLiveProfMethods;
    private JTextField txtGetLiveProfMethodInfo;
    private JTextField txtGetLiveProfUrl;
    private JTextField txtGetLiveProfGraphsUrl;

    private ArrayList<SettingHolder> varHolders = new ArrayList<>();
    private JTabbedPane tabbedPane1;

    private JCheckBox chkEnableLiveProfiler;
    private JCheckBox chkEnableLiveProfilerMethodInfo;

    private Vector<Integer> liveProfItemToSeconds = new Vector<Integer>(Arrays.asList(
            0,
            3600,
            3600 * 2,
            3600 * 3,
            3600 * 4,
            3600 * 6,
            3600 * 8,
            3600 * 12,
            3600 * 24
    ));

    private boolean changed = false;

    public SettingsForm() {
        chkEnableDebug.addChangeListener(e -> { changed = true; });

        btnUpdateLiveProfMethodsNow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LiveProfLoader loader = new LiveProfLoader();
                loader.loadMethods();
            }
        });
        sldLiveProfMethodsUpdateInterval.addActionListener(e -> changed = true);

        addParam(Settings.getInstance(), "enableLiveProfiler", chkEnableLiveProfiler);
        addParam(Settings.getInstance(), "enableLiveProfilerMethodInfo", chkEnableLiveProfilerMethodInfo);
        addParam(Settings.getInstance().urls, "getLiveProfMethods", txtGetLiveProfMethods);
        addParam(Settings.getInstance().urls, "getLiveProfMethodInfo", txtGetLiveProfMethodInfo);
        addParam(Settings.getInstance().urls, "getLiveProfUrl", txtGetLiveProfUrl);
        addParam(Settings.getInstance().urls, "getLiveProfGraphsUrl", txtGetLiveProfGraphsUrl);
    }

    private void addParam(Object object, String fieldName, JTextField editor) {
        Field field = FieldUtils.getField(object.getClass(), fieldName);
        varHolders.add(new StringSettingHolder(field, object, editor));
    }

    private void addParam(Object object, String fieldName, TextFieldWithHistoryWithBrowseButton editor) {
        Field field = FieldUtils.getField(object.getClass(), fieldName);
        varHolders.add(new StringSetHistorySettingHolder(field, object, editor));
    }

    private void addParam(Object object, String fieldName, JComboBox<Object> editor) {
        Field field = FieldUtils.getField(object.getClass(), fieldName);
        varHolders.add(new StringSetSettingHolder(field, object, editor));
    }

    private void addParam(Object object, String fieldName, JCheckBox editor) {
        Field field = FieldUtils.getField(object.getClass(), fieldName);
        varHolders.add(new BooleanSettingHolder(field, object, editor));
    }

    @Override
    public String getDisplayName() {
        return "Live Profiler";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        if (changed) {
            return true;
        }

        for (SettingHolder varHolder : varHolders) {
            if (varHolder.isChanged()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        Settings.getInstance().enableDebug = chkEnableDebug.isSelected();

        Settings.getInstance().refreshLiveProfMethodsSecs = liveProfItemToSeconds.elementAt(
                sldLiveProfMethodsUpdateInterval.getSelectedIndex()
        );

        if (chkEnableLiveProfiler.isSelected() && !Settings.getInstance().enableLiveProfiler) {
            LiveProfLoader loader = new LiveProfLoader();
            loader.loadMethods();
        }

        for (SettingHolder varHolder : varHolders) {
            varHolder.commit();
        }
        changed = false;
    }

    @Override
    public void reset() {
        // Let's pretend it's a simple label
        // using TextPane, though, is to make possible to copy-paste content and to make custom formatting

        chkEnableDebug.setSelected(Settings.getInstance().enableDebug);

        var item = liveProfItemToSeconds.indexOf(Settings.getInstance().refreshLiveProfMethodsSecs);
        if (item == -1) {
            item = 5; // every 6 hours by default
        }
        sldLiveProfMethodsUpdateInterval.setSelectedIndex(item);

        for (SettingHolder varHolder : varHolders) {
            varHolder.reset();
        }

        changed = false;
    }
}

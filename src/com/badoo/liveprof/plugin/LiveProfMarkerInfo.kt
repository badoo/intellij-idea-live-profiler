package com.badoo.liveprof.plugin

import com.badoo.liveprof.plugin.*
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.execution.lineMarker.LineMarkerActionWrapper
import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.MarkupEditorFilter
import com.intellij.openapi.editor.markup.MarkupEditorFilterFactory
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.PhpLanguage
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import java.net.URLEncoder

class LiveProfMarkerInfo(private val psiElement : PsiElement) : LineMarkerInfo<PsiElement>(
        psiElement,
        psiElement.textRange,
        Icons.liveprofIcon,
        { _ -> "Live Profiler" },
        null,
        GutterIconRenderer.Alignment.LEFT
) {
    val actionGroup = DefaultActionGroup()

    init {
        val phpClass = psiElement.parent.parent as PhpClass
        val phpMethod = psiElement.parent as Method

        var full_method_name = phpClass.fqn + "." + phpMethod.name
        val date = LiveProfStorage.getInstance().get(full_method_name)

        actionGroup.add(
                LineMarkerActionWrapper(psiElement, object : AnAction("Method usage (" + date + ")") {
                    override fun actionPerformed(e: AnActionEvent) {
                        if (psiElement.isValid) {
                            val url = Settings.getInstance().urls!!.getLiveProfUrl!!
                            BrowserLauncher.instance.browse(url + phpClass.fqn + "::" + phpMethod.name)
                        }
                    }
                })
        )
    }

    override fun createGutterRenderer(): GutterIconRenderer? {
        return object : LineMarkerInfo.LineMarkerGutterIconRenderer<PsiElement>(this) {
            override fun getClickAction(): AnAction? {
                if (!Settings.getInstance().enableLiveProfilerMethodInfo!!) {
                    return null
                }

                // The menu is alredy loaded
                if (actionGroup.getChildrenCount() > 1) {
                    return null
                }

                val phpClass = psiElement.parent.parent as PhpClass
                val phpMethod = psiElement.parent as Method
                var full_method_name = phpClass.fqn + "." + phpMethod.name

                val loader = LiveProfLoader()
                val fullMethodName = phpClass.fqn + "::" + phpMethod.name
                val methodInfo = loader.loadMethodInfo(fullMethodName)
                if (methodInfo == null) {
                    return null
                }

                if (methodInfo.size > 0) {
                    actionGroup.addSeparator()
                }

                val methodLastUseDate = LiveProfStorage.getInstance().get(full_method_name)
                for (info in methodInfo) {
                    var dateStr = ""
                    if (info.date != methodLastUseDate) {
                        // Add only different date
                        dateStr = " (" + info.date + ")"
                    }

                    val appLabel = info.app + " " + info.cpu + " ms" + ",  " + "%.1f".format(info.ct) + " calls" + ",  " + info.calls_count + " req." + dateStr
                    actionGroup.add(
                            LineMarkerActionWrapper(psiElement, object : AnAction(appLabel) {
                                override fun actionPerformed(e: AnActionEvent) {
                                    if (psiElement.isValid) {
                                        val url = Settings.getInstance().urls!!.getLiveProfGraphsUrl!!
                                        BrowserLauncher.instance.browse(url + "app=" + info.app + "&label=" + URLEncoder.encode(info.label, "UTF-8") + "&method_name=" + URLEncoder.encode(fullMethodName, "UTF-8"))
                                    }
                                }
                            })
                    )
                }

                return null
            }
            override fun isNavigateAction(): Boolean = true
            override fun getPopupMenuActions(): ActionGroup? = actionGroup
        }
    }
}

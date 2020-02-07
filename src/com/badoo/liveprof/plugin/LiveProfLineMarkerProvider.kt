package com.badoo.liveprof.plugin

import com.badoo.liveprof.plugin.*
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.execution.*
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.lineMarker.LineMarkerActionWrapper
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.PhpLanguage
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.patterns.PlatformPatterns
import java.net.URLEncoder

typealias pp = PlatformPatterns

class LiveProfLineMarkerProvider : LineMarkerProvider {
    private val methodPattern =
            pp.psiElement(PhpTokenTypes.kwFUNCTION)
                    .withParent(
                            pp.psiElement(Method::class.java)
                                    .withParent(pp.psiElement(PhpClass::class.java))
                    )
                    .withLanguage(PhpLanguage.INSTANCE)

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (!Settings.getInstance().enableLiveProfiler!!) {
            return null
        }

        val (phpClass, phpMethod) =
                when {
                    methodPattern.accepts(element) -> arrayOf(element.parent.parent as? PhpClass, element.parent)
                    else -> return null
                }

        if (phpClass !is PhpClass) {
            return null
        }

        if (phpMethod !is Method) {
            return null
        }

        val full_method_name = phpClass.fqn + "." + phpMethod.name
        val date = LiveProfStorage.getInstance().get(full_method_name)
        if (date == null) {
            return null
        }

        val markerInfo = LiveProfMarkerInfo(element)

        return markerInfo
    }
}

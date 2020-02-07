package com.badoo.liveprof.plugin.test

import com.badoo.liveprof.plugin.LiveProfMarkerInfo
import com.badoo.liveprof.plugin.LiveProfStorage
import com.badoo.liveprof.plugin.Settings
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.impl.LineMarkersPass
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import junit.framework.TestCase

class LineMarkerTest  : LightCodeInsightFixtureTestCase() {
    override fun getTestDataPath() =
            "src/test/kotlin/com/badoo/liveprof/plugin/test/testData/"

    override fun tearDown() {
        super.tearDown()
    }

    fun testAllIsOk()
    {
        Settings.getInstance().enableLiveProfiler = true

        val methods = mapOf("SomeClass::someMethod" to "2020-01-01", "SomeClass::anotherMethod" to "2020-01-02")
        LiveProfStorage.getInstance().update(methods)

        myFixture.configureByFiles("LineMarker1.php")
        val marks: Collection<LineMarkerInfo<*>> = LineMarkersPass.queryLineMarkers(myFixture.file, myFixture.getDocument(myFixture.file))
                .filter {
                    it is LiveProfMarkerInfo
                }

        TestCase.assertEquals(2, marks.size)

        for (mark in marks) {
            TestCase.assertEquals("Live Profiler", mark.getLineMarkerTooltip())
        }
    }
}
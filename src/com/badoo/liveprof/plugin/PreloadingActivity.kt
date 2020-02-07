package com.badoo.liveprof.plugin

import com.intellij.openapi.application.PreloadingActivity
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.ProjectManager
import com.badoo.liveprof.plugin.Notification;
import com.badoo.liveprof.plugin.Settings

class PreloadingActivity : PreloadingActivity() {
    override fun preload(p0: ProgressIndicator) {
        if (Settings.getInstance().enableLiveProfiler!!) {
            val loader = LiveProfLoader()
            loader.loadMethods()
        }

        var liveProfDelay = Settings.getInstance().refreshLiveProfMethodsSecs!!
        if (liveProfDelay < 3600) { // no more often than once in 1 hour
            liveProfDelay = 3600
        }
        val day = 3600 * 24
        if (liveProfDelay > day) {
            liveProfDelay = day
        }
        Notification.debug("Component LiveProf methods update interval: " + liveProfDelay + " seconds")
        TimerStorage.addTimer(liveProfDelay / 60) {
            ProgressManager.getInstance().run(UpdateLiveProfMethodsTask())
        }
    }

    private class UpdateLiveProfMethodsTask : Task.Backgroundable(ProjectManager.getInstance().defaultProject, "Updating LiveProf methods")
    {
        override fun run(p0: ProgressIndicator) {
            p0.fraction = 0.0
            ProgressManager.getInstance().executeNonCancelableSection(Runnable {
                if (Settings.getInstance().enableLiveProfiler!!) {
                    val loader = LiveProfLoader()
                    loader.loadMethods()
                }
                p0.fraction = 0.9
            })
            p0.fraction = 1.0
        }
    }
}

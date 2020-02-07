import com.intellij.openapi.application.ApplicationManager
import java.awt.event.ActionListener
import javax.swing.Timer
import kotlin.collections.ArrayList

object TimerStorage {
    private val timers: ArrayList<Timer> = arrayListOf()
    private var disposed = false

    fun addTimer(minutes: Int, cb: () -> Unit ) {
        addTimerSeconds(minutes * 60, cb)
    }

    fun addTimerSeconds(seconds: Int, cb: () -> Unit): Timer? {
        synchronized(this) {
            if (disposed) {
                return@addTimerSeconds null
            }
            val timer = Timer(
                    seconds * 1000,
                    ActionListener {
                        ApplicationManager.getApplication().invokeLater {
                            cb()
                        }
                    }
            )
            timers.add(timer)

            timer.isRepeats = true
            timer.start()

            return@addTimerSeconds timer
        }
    }

    fun disposeTimers() {
        synchronized(this) {
            disposed = true
        }
        timers.forEach { t ->
            t.stop()
        }
        timers.clear()
    }
}
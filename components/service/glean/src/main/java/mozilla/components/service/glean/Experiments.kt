package mozilla.components.service.glean

import android.content.Context
import kotlinx.coroutines.*
import mozilla.telemetry.glean.Experiments as ExperimentsCore

object Experiments {
    private var inner: ExperimentsCore = ExperimentsCore
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO) + job
    fun initialize(applicationContext: Context, dbPath: String) {
        inner.initialize(applicationContext, dbPath)
    }
    fun getBranch(experimentName: String): String {
        return inner.getBranch(experimentName)
    }
}
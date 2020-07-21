/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.samples.glean

import android.app.Application
import android.content.Intent
import kotlinx.coroutines.*
import mozilla.components.lib.fetch.httpurlconnection.HttpURLConnectionClient
import mozilla.components.service.experiments.Configuration as ExperimentsConfig
import mozilla.components.service.glean.Glean
import mozilla.components.service.glean.Experiments as Exp
import mozilla.components.service.glean.config.Configuration
import mozilla.components.service.glean.net.ConceptFetchHttpUploader
import mozilla.components.service.experiments.Experiments
import mozilla.components.service.glean.RustHttpConfig
import mozilla.components.support.base.log.Log
import mozilla.components.support.base.log.sink.AndroidLogSink
import org.mozilla.samples.glean.GleanMetrics.Basic
import org.mozilla.samples.glean.GleanMetrics.Test
import org.mozilla.samples.glean.GleanMetrics.Custom
import org.mozilla.samples.glean.GleanMetrics.Pings
import kotlin.coroutines.CoroutineContext


class GleanApplication : Application() {

    override fun onCreate() {
            super.onCreate()
            // We want the log messages of all builds to go to Android logcat
            Log.addSink(AndroidLogSink())

            // Register the sample application's custom pings.
            Glean.registerPings(Pings)

            // Initialize the Glean library. Ideally, this is the first thing that
            // must be done right after enabling logging.
            val client by lazy { HttpURLConnectionClient() }
            val httpClient = ConceptFetchHttpUploader.fromClient(client)
            val config = Configuration(httpClient = httpClient)
            Glean.initialize(applicationContext, uploadEnabled = true, configuration = config)
            RustHttpConfig.setClient(lazy { HttpURLConnectionClient() })
            Thread {
                GlobalScope.launch {
                    var exp = mozilla.components.service.glean.Experiments
                    exp.initialize(applicationContext, applicationContext.dataDir.path)
                    val res = exp.getBranch("button-color")
                    println(res)
                    //TODO
                    // Send an enrolled event for the experiment
                    // Send a saw button-color button. AKA "User saw some branch of the experiment"
                }
            }.start()
            // Initialize the Experiments library and pass in the callback that will generate a
            // broadcast Intent to signal the application that experiments have been updated. This is
            // only relevant to the experiments library, aside from recording the experiment in Glean.
            Experiments.initialize(applicationContext, ExperimentsConfig(httpClient = client)) {
                val intent = Intent()
                intent.action = "org.mozilla.samples.glean.experiments.updated"
                sendBroadcast(intent)
            }

            Test.timespan.start()

            Custom.counter.add()

            // Set a sample value for a metric.
            Basic.os.set("Android")
        }
    }

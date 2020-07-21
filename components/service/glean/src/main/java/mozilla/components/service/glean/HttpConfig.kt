package mozilla.components.service.glean


import mozilla.components.concept.fetch.Client
import mozilla.telemetry.glean.RustHttpConfig as HttpConfig

/**
 * An object allowing configuring the HTTP client used by Rust code.
 */
object RustHttpConfig {

    /**
     * Set the HTTP client to be used by all Rust code.
     *
     * The `Lazy`'s value is not read until the first request is made.
     *
     * This must be called
     * - after initializing a megazord for users using a custom megazord build.
     * - before any other calls into application-services rust code which make HTTP requests.
     */
    fun setClient(c: Lazy<Client>) {
        HttpConfig.setClient(c)
    }
}
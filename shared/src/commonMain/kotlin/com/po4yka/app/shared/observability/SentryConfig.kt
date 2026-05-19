package com.po4yka.app.shared.observability

import com.po4yka.app.shared.BuildKonfig
import io.sentry.kotlin.multiplatform.Sentry

/**
 * Initialize Sentry once at app start.
 *
 * No-ops when SENTRY_DSN is empty so local builds and tests don't ship telemetry.
 * Wire credentials via `-PsentryDsn=...` (CI secret) or `local.properties` — see docs/sentry.md.
 */
fun initSentry() {
    val dsn = BuildKonfig.SENTRY_DSN
    if (dsn.isEmpty()) return

    Sentry.init { options ->
        options.dsn = dsn
        options.release = BuildKonfig.APP_NAME
        // Sampling defaults are conservative — tune in docs/sentry.md once a baseline lands.
        options.tracesSampleRate = 0.0
    }
}
